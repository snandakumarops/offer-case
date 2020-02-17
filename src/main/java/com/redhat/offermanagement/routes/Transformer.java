package com.redhat.offermanagement.routes;

public class Transformer {

    public String txnTransform(String body) {
        String caseStr = "{ \"case-data\" : ";
        return caseStr+body+"}";
    }
}
