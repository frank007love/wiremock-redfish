package org.tonylin.wiremock.redfish.extensions;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class SocketTimedOutResponseTransformer extends ResponseTransformer {

    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
    	if(response.getStatus()==500&&response.getBodyAsString().contains("timed out")) {
    		return Response.Builder.like(response)
    				.body(String.valueOf(Fault.CONNECTION_RESET_BY_PEER))
    				.fault(Fault.CONNECTION_RESET_BY_PEER).build();
    	}
        return response;
    }

    @Override
    public String getName() {
        return "SocketTimedOutResponseTransformer";
    }
}