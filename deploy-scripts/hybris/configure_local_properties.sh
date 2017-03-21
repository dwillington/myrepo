rm -rf /root/local.properties
mv /opt/hybris/hybris/config/local.properties /root/local.properties
sed -i -e "s/bazaarvoice.feed.db.*//g" /root/local.properties
sed -i -e "s/optimizedprice.db.*//g" /root/local.properties
sed -i -e "s/db.url.*//g" /root/local.properties
sed -i -e "s/db.username.*//g" /root/local.properties
sed -i -e "s/db.password.*//g" /root/local.properties
sed -i -e "s/db.driver.*//g" /root/local.properties

sed -i -e "s/^depotdirect.baseuri.*/depotdirect.baseuri=http:\/\//g" /root/local.properties
sed -i -e "s/^depotdirect.productavailability.path.*/depotdirect.productavailability.path=mock-service:4546\/MMSVAOLCA\/rest\/ProductAvailabilityService\/getProductAvailability/g" /root/local.properties
sed -i -e "s/^depotdirect.validatepostalcode.path.*/depotdirect.validatepostalcode.path=mock-service:4545\/MMSVAOLCA\/rest\/PostalCodeValidationService\/validatePostalCode/g" /root/local.properties
sed -i -e "s/^depotdirect.deliverySlots.path.*/depotdirect.deliverySlots.path=mock-service:4555\/MMSVAOLCA\/rest\/DeliveryCalendarService\/getDeliverySlots/g" /root/local.properties
sed -i -e "s/^depotdirect.relatedServices.path.*/depotdirect.relatedServices.path=mock-service:4556\/MMSVAOLCA\/rest\/RelatedServicesService\/getRelatedServices/g" /root/local.properties
sed -i -e "s/^depotdirect.reservedeliveryslot.path.*/depotdirect.reservedeliveryslot.path=mock-service:4559\/MMSVAOLCA\/rest\/DeliveryCalendarService\/reserveDeliverySlot/g" /root/local.properties
sed -i -e "s/^sap.submitorder.url.*/sap.submitorder.url=https:\/\/mock-service:4557\/\/XISOAPAdapter\/MessageServlet?channel=:QHDWCS:Sender_SOAP_WCSOrderCreate/g" /root/local.properties
sed -i -e "s/^sap.orderdetail.url.*/sap.orderdetail.url=https:\/\/mock-service:4554\/\/XISOAPAdapter\/MessageServlet?channel=:QHDWCS:Sender_SOAP_WCSOrderDetail/g" /root/local.properties
sed -i -e "s/^sap.orderlist.url.*/sap.orderlist.url=https:\/\/mock-service:4553\/\/XISOAPAdapter\/MessageServlet?channel=:QHDWCS:Sender_SOAP_WCSOrderList/g" /root/local.properties
sed -i -e "s/^sap.ws.inventory.uri.*/sap.ws.inventory.uri=http:\/\/mock-service:4547\/\/XISOAPAdapter\/MessageServlet?channel=:QHDCA:Sender_SOAP_HDCAInventoryLookup/g" /root/local.properties
sed -i -e "s/^certona.resonance.url.*/certona.resonance.url=http:\/\/mock-service:4551\/ws\/r2\/resonance.aspx/g" /root/local.properties
sed -i -e "s/^hdcreditservices.uri.*/hdcreditservices.uri=http:\/\/mock-service:4558\/RetailAuth\/services\/PaymentAuthorizationService/g" /root/local.properties
sed -i -e "s/^tandem.url.*/tandem.url=https:\/\/mock-service:4561\/XISOAPAdapter\/MessageServlet?channel=:QHDWCS:Sender_SOAP_TandemXRef/g" /root/local.properties
sed -i -e "s/^crmComplaint.url.*/crmComplaint.url=https:\/\/mock-service:4552\/XISOAPAdapter\/MessageServlet?channel=:QHDCA:Sender_SOAP_HDCAContactUsCreateComplaint/g" /root/local.properties
sed -i -e "s/^cybersource.uri.*/cybersource.uri=https:\/\/mock-service:4560\/commerce\/1.x\/transactionProcessor/g" /root/local.properties

/bin/cp -rf /root/local.properties.db.base.orig /root/local.properties.db.base
export my_host_name=`hostname`
export epic_name=${my_host_name%-*}
sed -i -e "s/mysql-hostname/$epic_name-mysql/g" /root/local.properties.db.base

cat /root/local.properties.db.base >> /root/local.properties
/bin/cp -rf /root/local.properties /opt/hybris/hybris/config/local.properties