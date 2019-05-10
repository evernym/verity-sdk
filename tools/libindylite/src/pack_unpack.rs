extern crate sodiumoxide;

use sodiumoxide::crypto::sign;
use sodiumoxide::crypto::aead::chacha20poly1305_ietf;

#[derive(Serialize, Deserialize, Debug, Clone, Eq, PartialEq)]
pub struct JWE {
    pub protected: String,
    pub iv: String,
    pub ciphertext: String,
    pub tag: String

}

#[derive(Serialize, Deserialize, Debug, Clone, Eq, PartialEq)]
pub struct Recipient {
    pub encrypted_key: String,
    pub header: Header
}

#[derive(Serialize, Deserialize, Debug, Clone, Eq, PartialEq)]
pub struct Header {
    pub kid: String,
    #[serde(default)]
    #[serde(skip_serializing_if = "Option::is_none")]
    pub iv: Option<String>,
    #[serde(default)]
    #[serde(skip_serializing_if = "Option::is_none")]
    pub sender: Option<String>
}

#[derive(Serialize, Deserialize, Debug, Clone, Eq, PartialEq)]
pub struct Protected {
    pub enc: String,
    pub typ: String,
    pub alg: String,
    pub recipients: Vec<Recipient>,
}

#[derive(Serialize, Deserialize, Debug, Clone, Eq, PartialEq)]
pub struct UnpackMessage {
    pub message: String,
    pub recipient_verkey: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub sender_verkey: Option<String>
}

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
    tokeys: Vec<[u8]>,
    keypair: Option<(Vec<[u8]>, Vec<[u8]>)>,
) {
//  Init libsodium : TODO init only once
    sodiumoxide::init();

//    If keypair exists, authcrypt else anoncrypt
    let base64_protected = if let Some(keypair) = sender_vk {
        prepare_protected_authcrypt(&cek, receivers, keypair)?
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

fn prepare_rec_keys(toKeys: Vec<[u8]>,
                    keypair: Option<(sign::PublicKey, sign::SecretKey)>) {

    //  Generate a symmetric content encryption key (CEK)
    let cek = chacha20poly1305_ietf::gen_key();
    let mut encrypted_recipients_struct : Vec<Recipient> = vec![];

    for recipient in toKeys {

        let enc_cek: Vec<u8>;

        if Some(keypair) {
            let senderVk = 
            enc_cek = sodiumoxide::crypto::box_::seal()
        } else {
            enc_cek = sodiumoxide::crypto::sealedbox::seal(&recipient, &cek[..]);
        }
    }
}

fn prepare_protected_authcrypt(cek: &chacha20poly1305_ietf::Key,
                               receiver_list: Vec<String>,
                               keypair: (sign::PublicKey, sign::SecretKey)) {

}

fn prepare_protected_anoncrypt(cek: &chacha20poly1305_ietf::Key, receiver_list: Vec<sign::PublicKey>){
    let mut encrypted_recipients_struct : Vec<Recipient> = vec![];

    for their_vk in receiver_list {
        //encrypt sender verkey
        let enc_cek = sodiumoxide::crypto::sealedbox::seal(&their_vk, &cek[..]);

        //create recipient struct and push to encrypted list
        encrypted_recipients_struct.push(Recipient {
            encrypted_key: base64::encode_urlsafe(enc_cek.as_slice()),
            header: Header {
                kid: their_vk,
                sender: None,
                iv: None
            },
        });
    } // end for-loop
    Ok(self._base64_encode_protected(encrypted_recipients_struct, false)?)
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