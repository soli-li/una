# keystore
## CA
### keystore_ca.p12
`keytool -genkeypair -alias soli_ca -keystore keystore_ca.p12 -keypass soli_pass -keyalg RSA -keysize 2048 -sigalg SHA512withRSA -validity 36500 -storepass soli_pass -storetype PKCS12 -ext SAN=dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1 -ext KeyUsage=digitalSignature,keyCertSign -ext "BasicConstraints=ca:true,PathLen:-1" -dname "CN=soliSign, OU=soli, O=soli, L=gz, ST=gd, C=cn"`

### ca.cer
`keytool -exportcert -alias soli_ca -keystore keystore_ca.p12 -storepass soli_pass -file ca.cer`

## manager
### keystore_RSA_manager.p12
`keytool -genkeypair -alias una_manager -keystore keystore_RSA_manager.p12 -keypass una_pass -keyalg RSA -keysize 2048 -sigalg SHA512withRSA -validity 3650 -storepass una_pass -storetype PKCS12 -ext SAN=dns:system-manager,dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -dname "CN=system-manager, OU=soli, O=soli, L=gz, ST=gd, C=cn"`

### manager.csr
`keytool -certreq -alias una_manager -keystore keystore_RSA_manager.p12 -keyalg rsa -keysize 2048 -storepass una_pass -file manager.csr`

## gateway
### keystore_RSA_gateway.p12
`keytool -genkeypair -alias una_gateway -keystore keystore_RSA_gateway.p12 -keypass una_pass -keyalg RSA -keysize 2048 -sigalg SHA512withRSA -validity 3650 -storepass una_pass -storetype PKCS12 -ext SAN=dns:system-gateway,dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -dname "CN=system-gateway, OU=soli, O=soli, L=gz, ST=gd, C=cn"`

### gateway.csr
`keytool -certreq -alias una_gateway -keystore keystore_RSA_gateway.p12 -keyalg rsa -keysize 2048 -storepass una_pass -file gateway.csr`

## notification
### keystore_RSA_notification.p12
`keytool -genkeypair -alias una_notification -keystore keystore_RSA_notification.p12 -keypass una_pass -keyalg RSA -keysize 2048 -sigalg SHA512withRSA -validity 3650 -storepass una_pass -storetype PKCS12 -ext SAN=dns:system-notification,dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -dname "CN=system-notification, OU=soli, O=soli, L=gz, ST=gd, C=cn"`

### notification.csr
`keytool -certreq -alias una_notification -keystore keystore_RSA_notification.p12 -keyalg rsa -keysize 2048 -storepass una_pass -file notification.csr`

## sharing
### keystore_RSA_sharing.p12
`keytool -genkeypair -alias una_sharing -keystore keystore_RSA_sharing.p12 -keypass una_pass -keyalg RSA -keysize 2048 -sigalg SHA512withRSA -validity 3650 -storepass una_pass -storetype PKCS12 -ext SAN=dns:system-sharing,dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -dname "CN=system-sharing, OU=soli, O=soli, L=gz, ST=gd, C=cn"`

# sharing.csr
`keytool -certreq -alias una_sharing -keystore keystore_RSA_sharing.p12 -keyalg rsa -keysize 2048 -storepass una_pass -file sharing.csr`

# gen cer
### manager.cer
`keytool -gencert -alias soli_ca -keystore keystore_ca.p12 -validity 3650 -storepass soli_pass -infile manager.csr -outfile manager.cer -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SAN=dns:system-manager,dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1`

### gateway.cer
`keytool -gencert -alias soli_ca -keystore keystore_ca.p12 -validity 3650 -storepass soli_pass -infile gateway.csr -outfile gateway.cer -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SAN=dns:system-manager,dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1`

### notification.cer
`keytool -gencert -alias soli_ca -keystore keystore_ca.p12 -validity 3650 -storepass soli_pass -infile notification.csr -outfile notification.cer -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SAN=dns:system-notification,dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1`

### sharing.cer
`keytool -gencert -alias soli_ca -keystore keystore_ca.p12 -validity 3650 -storepass soli_pass -infile sharing.csr -outfile sharing.cer -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SAN=dns:system-sharing,dns:localhost,dns:ip6-localhost,ip:127.0.0.1,ip:::1`

# import
`keytool -importcert -alias soli_ca -keystore keystore_RSA_manager.p12 -storepass una_pass -file ca.cer`  
`keytool -importcert -alias soli_ca -keystore keystore_RSA_gateway.p12 -storepass una_pass -file ca.cer`  
`keytool -importcert -alias soli_ca -keystore keystore_RSA_notification.p12 -storepass una_pass -file ca.cer`  
`keytool -importcert -alias soli_ca -keystore keystore_RSA_sharing.p12 -storepass una_pass -file ca.cer`  

`keytool -importcert -alias una_manager -keystore keystore_RSA_manager.p12 -storepass una_pass -file manager.cer`  
`keytool -importcert -alias una_gateway -keystore keystore_RSA_gateway.p12 -storepass una_pass -file gateway.cer`  
`keytool -importcert -alias una_notification -keystore keystore_RSA_notification.p12 -storepass una_pass -file notification.cer`  
`keytool -importcert -alias una_sharing -keystore keystore_RSA_sharing.p12 -storepass una_pass -file sharing.cer`  

# gen pem
`keytool -exportcert -alias una_manager -keystore keystore_RSA_manager.p12 -storepass una_pass -file manager.pem -rfc`  
`keytool -exportcert -alias una_gateway -keystore keystore_RSA_gateway.p12 -storepass una_pass -file gateway.pem -rfc`  
`keytool -exportcert -alias una_notification -keystore keystore_RSA_notification.p12 -storepass una_pass -file notification.pem -rfc`  
`keytool -exportcert -alias una_sharing -keystore keystore_RSA_sharing.p12 -storepass una_pass -file sharing.pem -rfc`  

# import ca to default cacerts
`keytool -importcert -alias soli_ca -cacerts -storepass changeit -file ca.cer`