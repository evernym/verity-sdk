extern crate sodiumoxide;

use sodiumoxide::crypto::sign;
use sodiumoxide::crypto::aead::chacha20poly1305_ietf;

pub extern fn indylite_generate_keypair() -> (sodiumoxide::crypto::sign::ed25519::PublicKey, sodiumoxide::crypto::sign::ed25519::SecretKey) {
    sodiumoxide::init();
    sodiumoxide::crypto::sign::gen_keypair()
}

/**
    Pack
    message packing can be done in 1 of 2 ways: Anoncrypt or Authcrypt

    Anoncrypt
        When we are sending a message where the sender is anonymous.
    Authcrypt
        When the sender is revealed. The only one that can see who the sender is, is the
        recipient. Not the same as signatures.

    params
        message
        list of recipient keys
        sender keypair


    Steps
        Anoncrypt
            1. Generate a symmetric content encryption key (CEK)
            2. Encrypt the CEK for each recipients public key

        Authcrypt
            2. Generate a symmetric content encryption key (CEK)
*/
pub extern fn indylite_pack_message(
    message: Vec<u8>,
    receivers: &str,
    keypair: Option<(sign::PublicKey, sign::SecretKey)>,
) {
//  Init libsodium : TODO init only once
    sodiumoxide::init();

//  Generate a symmetric content encryption key (CEK)
    let cek = chacha20poly1305_ietf::gen_key();

    //parse receivers to structs
    let receiver_list: Vec<String> = serde_json::from_str(receivers).map_err(|err| {})?;

    //break early and error out if no receivers keys are provided
    if receiver_list.is_empty() {
        return Err(err_msg(IndyErrorKind::InvalidStructure, format!(
            "No receiver keys found"
        )));
    }

//    If keypair exists, authcrypt else anoncrypt
    let base64_protected = if let Some(keypair) = sender_vk {
        prepare_protected_authcrypt(&cek, receiver_list, keypair)?
    } else {
        prepare_protected_anoncrypt(&cek, receiver_list)?
    };

    // Use AEAD to encrypt `message` with "protected" data as "associated data"
//    let (ciphertext, iv, tag) =
//        self.crypto_service
//            .encrypt_plaintext(message, &base64_protected, &cek);
//
//    self._format_pack_message(&base64_protected, &ciphertext, &iv, &tag)

}

fn prepare_protected_authcrypt(cek: &chacha20poly1305_ietf::Key,
                               receiver_list: Vec<String>,
                               keypair: (sign::PublicKey, sign::SecretKey)) {

}

fn prepare_protected_anoncrypt(cek: &chacha20poly1305_ietf::Key, receiver_list: Vec<String>){

}

//
//pub extern fn indylite_unpack_message() {
//
//}

#[test]
fn test_generate_key_pair() {
    let tmp = indylite_generate_keypair();
    println!("tmp: {:?}", tmp);
    assert_eq!(2, 2);
}