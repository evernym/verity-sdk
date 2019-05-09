extern crate sodiumoxide;

use sodiumoxide::crypto::sign::ed25519;

pub extern fn indylite_generate_keypair() -> (sodiumoxide::crypto::sign::ed25519::PublicKey, sodiumoxide::crypto::sign::ed25519::SecretKey) {
    sodiumoxide::init();
    sodiumoxide::crypto::sign::gen_keypair()
}

pub extern fn indylite_pack_message() {

}

pub extern fn indylite_unpack_message() {

}

#[cfg(test)]
mod tests {
    use crate::indylite_generate_keypair;

    #[test]
    fn it_works() {
        assert_eq!(2 + 2, 4);
    }

    #[test]

    fn test_generate_key_pair() {
        println!(indylite_generate_keypair())
    }
}