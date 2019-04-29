/**
 * Writes the build number to the package.json in the build debian steps
 */

var COMMIT_DATE = ''
var COMMIT_HASH = ''
var TIME_OFFSET = 1500000000

require('child_process').exec('git rev-parse HEAD', function(err, stdout) {
    // Get the commit hash and date hash and assign to variables
    COMMIT_HASH = stdout.toString().slice(0,8)
    COMMIT_DATE = Math.floor(new Date().getTime() / 1000) - TIME_OFFSET 

    // Get package.json
    var fs = require('fs')
    var fileName = `${process.cwd()}/package.json`
    var file = require(fileName)

    // Write to package.json for build number in debain packager options
    var version = file.version.split("")
    version[version.length - 1] = `${COMMIT_DATE}.${COMMIT_HASH}`
    file.debianPackagerOptions.version = version.join("")
    fs.writeFile(fileName, JSON.stringify(file, null, 2), function(err) {
        if (err) return console.log(err)
        console.log(JSON.stringify(file));
        console.log('writing to ' + fileName);
    })
});
