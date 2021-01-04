package com.kedacom.haiou.kmtool.utils;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class ESClientManager {

    @Value("${es.server}")
    private String esHost;
    @Value("${es.port}")
    private Integer esPort;
    @Value("${es.cluster}")
    private String esClusterName;

    private Client client;

    public ESClientManager() {
    }

    public Client getClient() {
        if (this.client == null) {
            this.client = this.createClient();
        }

        return this.client;
    }

    private Client createClient() {
        if (this.client == null) {
            System.out.println("Creating client for Search!");

            try {
                Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", this.esClusterName).put("client.transport.sniff", true).build();
                TransportClient transportClient = new TransportClient(settings);
                String[] hosts = this.esHost.split(",");
                String[] arr$ = hosts;
                int len$ = hosts.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    String host = arr$[i$];
                    transportClient.addTransportAddress(new InetSocketTransportAddress(host.trim(), this.esPort));
                }

                if (transportClient.connectedNodes().size() == 0) {
                    System.err.println("There are no active nodes available for the transport!");
                }

                this.client = transportClient;
            } catch (Exception var8) {
                var8.printStackTrace();
                System.err.println("Error occured while creating search client!");
            }
        }

        return this.client;
    }

    @PreDestroy
    protected void destory() {
        System.out.println("destory the client");
        if (this.client != null) {
            this.client.close();
        }

    }
}
