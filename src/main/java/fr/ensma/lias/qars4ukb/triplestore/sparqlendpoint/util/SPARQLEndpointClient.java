/*********************************************************************************
* This file is part of QARS4UKB Project.
* Copyright (C) 2017 LIAS - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* QARS4UKB is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* QARS4UKB is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with QARS4UKB.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package fr.ensma.lias.qars4ukb.triplestore.sparqlendpoint.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Mickael BARON
 */
public class SPARQLEndpointClient {

    protected String url;
    
    protected String defaultGraphURI;
    
    protected OutputFormat outputFormat;
    
    protected URL baseURL;
    
    protected URL sparqlURL;
    
    protected Integer softLimit = Integer.MIN_VALUE;
    
    private SPARQLEndpointClient() {
    }
    
    public void setOutputFormat(OutputFormat outputFormat) {
	this.outputFormat = outputFormat;
    }

    /**
     * Queries the repository and returns the result in the requested format
     * 
     * @param sparql
     * @param format
     * @param softLimit
     * @return the result in the requested format
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    public String query(String sparql)
	    throws MalformedURLException, ProtocolException, IOException {
	OutputFormat refOutputFormat = null;
	
	if (outputFormat == null) {
	    refOutputFormat = OutputFormat.SPARQL_XML;
	} else {
	    refOutputFormat = outputFormat;
	}
	
	HttpURLConnection connection = (HttpURLConnection) this.getBaseURL().openConnection();

	connection.setDoOutput(true);
	connection.setDoInput(true);
	connection.setRequestMethod("POST");
	connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	connection.setRequestProperty("Accept", refOutputFormat.getMimeType());

	StringBuffer queryString = new StringBuffer();
	queryString.append("&query=" + URLEncoder.encode(sparql, "UTF-8"));
	
	if (softLimit != Integer.MIN_VALUE) {
	    queryString.append("&soft-limit=" + softLimit);
	}
	
	if (defaultGraphURI != null) {
	    queryString.append("&default-graph-uri=" + URLEncoder.encode(defaultGraphURI, "UTF-8"));
	}
	
	DataOutputStream ps = new DataOutputStream(connection.getOutputStream());
	ps.writeBytes(queryString.toString());

	ps.flush();
	ps.close();

	return readResponse(connection);
    }

    private String readResponse(HttpURLConnection connection)
	    throws MalformedURLException, ProtocolException, IOException {
	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

	StringBuilder responseBuilder = new StringBuilder();
	String str;
	while (null != ((str = in.readLine()))) {
	    responseBuilder.append(str + System.getProperty("line.separator"));
	}
	in.close();
	return responseBuilder.toString();
    }
    
    public URL getBaseURL() throws MalformedURLException {
	if (baseURL == null) {
	    baseURL = new URL(url);	    
	}
	return baseURL;
    }
    
    public String getURL() {
	return this.url;
    }
    
    public static class Builder {
	
	private String url;
	    
	private String defaultGraphURI;
	    
	private OutputFormat outputFormat;
	
	private Integer softLimit = Integer.MIN_VALUE;
	
	public Builder url(String url) {
	    this.url = url;
	    return this;
	}
	
	public Builder defaultGraphURI(String pDefaultGraphURI) {
	    this.defaultGraphURI = pDefaultGraphURI;
	    return this;
	}
	
	public Builder outputFormat(OutputFormat pOutputFormat) {
	    this.outputFormat = pOutputFormat;
	    return this;
	}
	
	public Builder softLimit(Integer pSoftLimit) {
	    this.softLimit = pSoftLimit;
	    return this;
	}
	
	public SPARQLEndpointClient build() {
	    SPARQLEndpointClient sparqlEndpointConfig = new SPARQLEndpointClient();
	    sparqlEndpointConfig.url = this.url;
	    sparqlEndpointConfig.defaultGraphURI = this.defaultGraphURI;
	    sparqlEndpointConfig.outputFormat = this.outputFormat;
	    sparqlEndpointConfig.softLimit = this.softLimit;
	    
	    return sparqlEndpointConfig;
	}
    }
}
