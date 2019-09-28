package main

import (
	"bytes"
	"crypto/x509"
	"encoding/pem"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
)

func sendEvent(stub shim.ChaincodeStubInterface) {
	submitterAsBytes, _ := stub.GetCreator()
	certStart := bytes.IndexAny(submitterAsBytes, "-----")
	if certStart == -1 {
		fmt.Errorf("No certificate found")
	}

	certText := submitterAsBytes[certStart:]
	bl, _ := pem.Decode(certText)
	if bl == nil {
		fmt.Errorf("Could not decode the PEM structure")
	}
	fmt.Println(string(certText))
	cert, err := x509.ParseCertificate(bl.Bytes)
	if err != nil {
		fmt.Errorf("ParseCertificate failed")
	}
	fmt.Println(cert)
	uname := cert.Subject.CommonName
	fmt.Println("Submitter:" + uname)
	err = stub.SetEvent("Submitter", []byte(uname))
	if err != nil {
		fmt.Errorf(err.Error())
	}
}
