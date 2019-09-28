package main

import (
	"bytes"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"errors"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

//加密
func RsaEncrypt(stub shim.ChaincodeStubInterface, cleartext string) (string, error) {
	tMap, err := stub.GetTransient()
	if err != nil {
		return "", err
	}
	publicKey := tMap["publicKey"]
	//解密pem格式的公钥
	block, _ := pem.Decode(publicKey)
	if block == nil {
		return "", errors.New("public key error")
	}
	// 解析公钥
	pubInterface, err := x509.ParsePKIXPublicKey(block.Bytes)
	if err != nil {
		return "", err
	}
	// 类型断言
	pub := pubInterface.(*rsa.PublicKey)
	//加密
	buffer := bytes.NewBufferString("")
	data, err := rsa.EncryptPKCS1v15(rand.Reader, pub, []byte(cleartext))
	if err != nil {
		return "", err
	}
	buffer.Write(data)
	return base64.RawURLEncoding.EncodeToString(buffer.Bytes()), nil
}

// 解密
func RsaDecrypt(stub shim.ChaincodeStubInterface, ciphertext string) (string, error) {
	tMap, err := stub.GetTransient()
	if err != nil {
		return "", err
	}
	privateKey := tMap["privateKey"]
	//解密
	block, _ := pem.Decode(privateKey)
	if block == nil {
		return "", errors.New("private key error!")
	}
	//解析PKCS1格式的私钥
	priv, err := x509.ParsePKCS1PrivateKey(block.Bytes)
	if err != nil {
		return "", err
	}
	// 解密
	buffer := bytes.NewBufferString("")
	data, err := base64.RawURLEncoding.DecodeString(ciphertext)
	decrypted, err := rsa.DecryptPKCS1v15(rand.Reader, priv, data)
	if err != nil {
		return "", err
	}
	buffer.Write(decrypted)
	return buffer.String(), err
}
