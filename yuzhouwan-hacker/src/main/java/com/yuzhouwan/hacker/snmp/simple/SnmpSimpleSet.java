package com.yuzhouwan.hacker.snmp.simple;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * Copyright @ 2016 yuzhouwan.com
 * All right reserved.
 * Function：snmp.simple
 *
 * @author Benedict Jin
 * @since 2015/11/24 0024
 */
public class SnmpSimpleSet {

    public static final int DEFAULT_VERSION = SnmpConstants.version2c;
    public static final String DEFAULT_PROTOCOL = "udp";
    public static final int DEFAULT_PORT = 161;
    public static final long DEFAULT_TIMEOUT = 3 * 1000L;
    public static final int DEFAULT_RETRY = 3;

    /**
     * Create communityTarget
     *
     * @param ip
     * @param community
     * @return CommunityTarget
     */
    private static CommunityTarget createDefault(String ip, String community) {

        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(DEFAULT_VERSION);
        target.setRetries(DEFAULT_RETRY);
        target.setTimeout(DEFAULT_TIMEOUT); // milliseconds
        return target;
    }

    /**
     * Set informations of equipment asynchronously.
     *
     * @param ip
     * @param community
     * @param oidStr
     */
    public static void snmpSyncSetList(String ip, String community, String oidStr) {

        CommunityTarget target = createDefault(ip, community);
        Snmp snmp = null;
        try {
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();

            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oidStr), new Integer32(123)));
            pdu.setType(PDU.SET);
            ResponseEvent event = snmp.send(pdu, target, null);
            System.out.println("PeerAddress:" + event.getPeerAddress());
            PDU response = event.getResponse();

            if (response == null) {
                System.out.print("response is null, request time out\r\n");

            } else {
                System.out.println("response pdu size is " + response.size());

                for (int i = 0; i < response.size(); i++) {
                    VariableBinding vb = response.get(i);
                    System.out.println(vb.getOid() + " = " + vb.getVariable());
                }
            }
            System.out.println("SNMP GET one OID value finished !");
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
