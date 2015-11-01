package com.myspace.user.bean;

import java.util.HashMap;

import javax.ejb.Local;

/**
 * @author Vineet Sandhu
 */
@Local
public interface IClientSessionBeanProviderLocal {
    
    public HashMap<String,IClientSessionBeanLocal> getUsers();
    public IClientSessionBeanLocal getUser(String userName);
    
}
