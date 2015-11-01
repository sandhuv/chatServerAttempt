
package com.myspace.helper;


import com.myspace.user.bean.IClientSessionBeanLocal;
import com.myspace.user.bean.IClientSessionBeanProviderLocal;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Bean provider Reference,  Clients call to get access
 * to singleton & stateful session beans.
 */
public class References {
    
    //Return ClientSessionBean local interface after lookup
    public IClientSessionBeanLocal lookupClientSessionBeanLocal() {
    	System.out.println("___lookupUserSessionBeanLocal___");
        try {
            Context c = new InitialContext();
            return (IClientSessionBeanLocal) c.lookup("java:global/chatAppEJBsEAR/chatAppEJBs/ClientSessionBean!com.myspace.user.bean.IClientSessionBeanLocal");
        } catch (NamingException ne) {
            System.out.println("lookupUserSessionBeanLocal:exception");
            throw new RuntimeException(ne);
        }
    }

    //Returns ClientSessionBeanProvider local interface after lookup
    public IClientSessionBeanProviderLocal lookupClientSessionBeanProviderLocal() {
    	System.out.println("__lookupUserAccessBeanLocal___");
        try {
            Context c = new InitialContext();
            return (IClientSessionBeanProviderLocal)c.lookup("java:global/chatAppEJBsEAR/chatAppEJBs/ClientSessionBeanProvider!com.myspace.user.bean.IClientSessionBeanProviderLocal");
        } catch (NamingException ne) {
            System.out.println("lookupUserAccessBeanLocal:exception");
            throw new RuntimeException(ne);
        }
    }  
}
