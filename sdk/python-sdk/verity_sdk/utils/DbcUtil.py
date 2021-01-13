class DbcUtil:
    """
    Utilities
    for design by contract.Allows for common pre-condition and post-condition checking.
    """

    @staticmethod
    def require(requirement: bool, msg: str):
        """
        Checks that given requirement is true, throws an ValueError if false
        :param requirement: testable requirement
        :param msg: string that is used to build exception message
        """
        if not requirement:
            raise ValueError('requirement failed: ' + msg)

    @staticmethod
    def require_not_null(arg, arg_name: str):
        """
        Checks and throws an ValueError if the given object is null
        :param arg: any object that can be null
        :param arg_name: name of the argument being checked, used in exception message if null
        """
        DbcUtil.require(arg is not None, 'required that ' + arg_name + ' must NOT be None')

    @staticmethod
    def require_string_not_null_or_empty(arg: str, arg_name: str):
        """
        Checks and throws an ValueError if the given string is null or empty
        :param arg: String argument which needs to be checked
        :param arg_name: name of the argument being checked, used in exception message if null or empty
        """
        DbcUtil.require(arg is not None and len(arg) > 0, 'required that ' + arg_name + ' must NOT be null or empty')

    @staticmethod
    def require_array_not_contain_null(array: list, arg_name: str):
        """
        Checks and throws an ValueError if the given array is null or contains null element
        :param array: Array argument which needs to be checked
        :param arg_name: name of the argument being checked, used in exception message
        """
        DbcUtil.require_not_null(array, arg_name)
        for x in array:
            DbcUtil.require(x, 'required that elements of ' + arg_name + ' must NOT be None')
