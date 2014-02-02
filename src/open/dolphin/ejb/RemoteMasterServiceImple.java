/*
 * Created on 2004/10/13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package open.dolphin.ejb;

import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import open.dolphin.dto.MasterSearchSpec;

/**
 * RemoteMasterServiceImple
 *
 * @author Minagawa,Kazushi
 *
 */
@Stateless
@SecurityDomain("openDolphin")
@RolesAllowed("user")
@Remote({RemoteMasterService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteMasterService")
public class RemoteMasterServiceImple implements RemoteMasterService {
    
    @PersistenceContext
    private EntityManager em;
    
    
    /**
     * マスタを検索する。
     */
    @RolesAllowed("user")
    public Collection getMaster(MasterSearchSpec spec) {
        
        Collection ret = null;
        
        switch (spec.getCode()) {
            
            case MasterSearchSpec.ADMIN_CLASS:
                // AdminValue From
                ret = em.createNamedQuery("adminClass").setParameter("hc1", spec.getFrom()).getResultList();
                break;
                
            case MasterSearchSpec.ADMINISTRATION:
                // AdminValue HierarchyCode1
                ret = em.createNamedQuery("admin").setParameter("hc2", spec.getHierarchyCode1()).getResultList();
                break;
                
            case MasterSearchSpec.ADMIN_COMENT:
                // AdminComentValue All
                ret = em.createNamedQuery("adminComment").getResultList();
                break;
                
            case MasterSearchSpec.RADIOLOGY_METHOD:
                // RadiologyMethodValue From
                ret = em.createNamedQuery("rdMethod").setParameter("hc1", spec.getFrom()).getResultList();
                break;
                
            case MasterSearchSpec.RADIOLOGY_COMENT:
                // RadiologyMethodValue HierarchyCode1
                ret = em.createNamedQuery("rdComment").setParameter("hc2", spec.getHierarchyCode1()).getResultList();
                break;
        }
        return ret;
        
    }
}
