package com.myspace.user.bean;

import javax.ejb.Singleton;
import com.myspace.helper.References;
import java.util.HashMap;
import javax.ejb.Lock;
import javax.ejb.LockType;

/** 
 * Singleton Bean that provides & maintains every client's stateful session beans. 
 * Clients call this Bean to get access to session beans for processing
 * business logic.
 * @author Vineet Sandhu
 */
@Singleton
public class ClientSessionBeanProvider implements IClientSessionBeanProviderLocal {
    
    private HashMap<String,IClientSessionBeanLocal> users;
    
    //Initialize properites
    public ClientSessionBeanProvider() {
        users = new HashMap<>();
    }
    
    //return users list
    @Lock(LockType.READ)
    public HashMap<String,IClientSessionBeanLocal> getUsers() {
        return users;
    }
    
    
    //Create an user entry, should the user is invoking a business method 
    //for the first time
    @Lock(LockType.READ)
    public IClientSessionBeanLocal getUser(String userName) {
        
        if(!users.containsKey(userName)) {
            createUser(userName);
        }
        return users.get(userName);
    }            
    
    //Create an entry for user with a corresponding stateful session bean 
    @Lock(LockType.WRITE)
    private void createUser(String userName) {
    	
    	System.out.println("Creating SessionBean for User  : " +userName);
    	
    	IClientSessionBeanLocal clientSessionBean = new References().lookupClientSessionBeanLocal();
        clientSessionBean.setUser(userName);
        users.put(userName, clientSessionBean);
       
    }
}
