package com.amazon.product.api.amazonassosiate.dao;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.amazon.product.api.amazonassosiate.SignedRequestsHelper;
import com.amazon.product.api.amazonassosiate.generated.Items;
import com.amazon.product.api.amazonassosiate.generated.Item;
import com.amazon.product.api.amazonassosiate.generated.ItemLookupResponse;
import com.amazon.product.api.amazonassosiate.generated.ItemSearchResponse;
import com.amazon.product.api.amazonassosiate.service.SearchItem;

@Repository
public class SearchItemDao implements SearchItem{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${AMAZON_ENDPOINT}")
	private String AMAZON_ENDPOINT;
	@Value("${AWS_ACCESS_KEY_ID}")
	private String AWS_ACCESS_KEY_ID;
	@Value("${AWS_SECRET_KEY}")
	private String AWS_SECRET_KEY;
	@Value("${ASSOCIATE_TAG}")
	private String AssociateTag;
	
	@Override
	public List<Items> searchItemByKeyword(String keyword){
		
		 	ItemSearchResponse response = null;
		 	URI uri = getURI(getSignedUrl(keyword));
	        try {
	            response = restTemplate.getForObject(uri, ItemSearchResponse.class);
	          } catch (RestClientException e) {
	            response = new ItemSearchResponse();
	          }
	         return response.getItems();
	}
	
	
	@Override
	public Item searchById(String idType, String id){
		ItemLookupResponse response = null;
		URI uri = getURI(getSignedUrlForIdSearch(idType, id));
		 try {
	            response = restTemplate.getForObject(uri, ItemLookupResponse.class);
	          } catch (RestClientException e) {
	        	  response = new ItemLookupResponse();
	          }
	         Items items = response.getItems().get(0);
	         return items.getItem().get(0);
	}
	
	private String getSignedUrlForIdSearch(String IdType, String ItemId){
	    String requestUrl = null;
	    
	    SignedRequestsHelper helper;

        try {
            helper = SignedRequestsHelper.getInstance(AMAZON_ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; 
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemLookup");
        params.put("AWSAccessKeyId", AWS_ACCESS_KEY_ID);
        params.put("AssociateTag", AssociateTag);
        params.put("IdType", IdType);
        params.put("ItemId", ItemId);
        params.put("ResponseGroup", "Accessories,Images,ItemAttributes,ItemIds,Offers,OfferFull,Reviews,SalesRank");

        requestUrl = helper.sign(params);

        System.out.println("Signed URL: \"" + requestUrl + "\"");
        
        return requestUrl;
}
	
	
	private String getSignedUrl(String keyword){
		    String requestUrl = null;
		    
		    SignedRequestsHelper helper;

	        try {
	            helper = SignedRequestsHelper.getInstance(AMAZON_ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ""; 
	        }

	        Map<String, String> params = new HashMap<String, String>();
	        params.put("Service", "AWSECommerceService");
	        params.put("Operation", "ItemSearch");
	        params.put("AWSAccessKeyId", AWS_ACCESS_KEY_ID);
	        params.put("AssociateTag", AssociateTag);
	        params.put("SearchIndex", "All");
	        params.put("Keywords", keyword);
	        params.put("ResponseGroup", "Images,ItemAttributes,ItemIds,OfferSummary,Reviews");

	        requestUrl = helper.sign(params);

	        System.out.println("Signed URL: \"" + requestUrl + "\"");
	        
	        return requestUrl;
	}
	
	private URI getURI(String requestUrl){
		URI uri  = null;
        try {
        	URL url = new URL(requestUrl);
			uri = url.toURI();
		} catch (URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return uri;
	}
	
	
	 /*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private static String fetchTitle(String requestUrl) {
        String title = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Node titleNode = doc.getElementsByTagName("Title").item(0);
            title = titleNode.getTextContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return title;
    }


}
