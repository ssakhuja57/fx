package listeners;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.fxcore2.IO2GResponseListener;
import com.fxcore2.O2GResponse;


public class ResponseListener implements IO2GResponseListener{

	private List<String> requestsPending;
	private List<String> requestsCompleted;
	private List<String> requestsFailed;
	private ConcurrentHashMap<String,O2GResponse> responses;
	
	public ResponseListener(){
		responses = new ConcurrentHashMap<String,O2GResponse>();
		requestsPending = new LinkedList<String>();
		requestsCompleted = new LinkedList<String>();
		requestsFailed = new LinkedList<String>();
	}
	
	@Override
	public void onRequestCompleted(String requestID, O2GResponse response) {
		responses.put(requestID, response);
		requestsCompleted.add(requestID);
		requestsPending.remove(requestID);
		//System.out.println("request type " + response.getType() + " completed");
	}

	@Override
	public void onRequestFailed(String requestID, String error) {
		if (requestID == null && error == null){
			return;
		}
		requestsFailed.add(requestID);
		requestsPending.remove(requestID);
		System.out.println("Request Failed with:");
		System.out.println(error);
		
	}
	

	@Override
	public void onTablesUpdates(O2GResponse arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void addRequestID(String requestID){
		requestsPending.add(requestID);
	}
	
	public String getRequestStatus(String requestID){
		if (requestsPending.contains(requestID)){
			return "PENDING";
		}
		else if (requestsCompleted.contains(requestID)){
			return "COMPLETED";
		}
		else if (requestsFailed.contains(requestID)){
			return "FAILED";
		}
		else{
			return "DOES NOT EXIST";
		}
	}
	
	public O2GResponse getResponse(String requestID, double waitFor, String description){
		for (int i=0;i<(1000*waitFor)/10;i++){
			try {
				Thread.currentThread().sleep(100);
				O2GResponse response = responses.get(requestID);
				if(response != null){
					System.out.println("received response (in " + i*0.1 + " sec): " + description);
					return response;
				}
				System.out.println("waiting for response: " + description);
			} catch (NullPointerException e){
				System.out.println("waiting for response: " + description);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		return null;
	
	}
	
	
	
	
}


