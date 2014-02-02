package open.dolphin.ejb;

import java.util.Collection;
import java.util.Iterator;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import open.dolphin.infomodel.AdminComentValue;
import open.dolphin.infomodel.AdminValue;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.RadiologyMethodValue;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserModel;

import org.jboss.annotation.ejb.RemoteBinding;

@Stateless
@Remote({RemoteSystemService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteSystemService")
public class RemoteSystemServiceImpl extends DolphinService implements RemoteSystemService {
    
    private static final String DEFAULT_FACILITY_OID = "1.3.6.1.4.1.9414.10.1";
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * 通信を確認する。
     * @return Hello, OpenDolphin文字列
     */
    public String helloDolphin() {
        return "Hello, OpenDolphin";
    }
    
    /**
     * 施設と管理者情報を登録する。
     *
     * @param user 施設管理者
     */
    public void addFacilityAdmin(UserModel user) {
        
        // OIDをセットし施設レコードを生成する
        FacilityModel facility = user.getFacilityModel();
        String facilityId = facility.getFacilityId();
        if (facilityId == null || facilityId.equals("")) {
            facilityId = DEFAULT_FACILITY_OID;
            facility.setFacilityId(facilityId);
        }
        
        try {
            em.createQuery("from FacilityModel f where f.facilityId = :fid")
            .setParameter("fid", facilityId)
            .getSingleResult();
            
            // すでに存在している場合は例外をスローする
            throw new EntityExistsException();
            
        } catch (NoResultException e) {
            // 当たり前
        }
        
        // 永続化する
        // このメソッドで facility が管理された状態になる
        em.persist(facility);
        
        // このユーザの複合キーを生成する
        // i.e. userId = facilityId:userId(local)
        StringBuilder sb = new StringBuilder();
        sb.append(facilityId);
        sb.append(COMPOSITE_KEY_MAKER);
        sb.append(user.getUserId());
        user.setUserId(sb.toString());
        
        // 上記 Facility の Admin User を登録する
        // admin と user Role を設定する
        boolean hasAdminRole = false;
        boolean hasUserRole = false;
        Collection<RoleModel> roles = user.getRoles();
        if (roles != null) {
            for (RoleModel val : roles) {
                if (val.getRole().equals(ADMIN_ROLE)) {
                    hasAdminRole = true;
                    continue;
                }
                if (val.getRole().equals(USER_ROLE)) {
                    hasUserRole = true;
                    continue;
                }
            }
        }
        
        if (!hasAdminRole) {
            RoleModel role = new RoleModel();
            role.setRole(ADMIN_ROLE);
            role.setUser(user);
            role.setUserId(user.getUserId());
            user.addRole(role);
        }
        
        if (!hasUserRole) {
            RoleModel role = new RoleModel();
            role.setRole(USER_ROLE);
            role.setUser(user);
            role.setUserId(user.getUserId());
            user.addRole(role);
        }
        
        // 永続化する
        // Role には User から CascadeType.ALL が設定されている
        em.persist(user);
        
    }
    
    /**
     * 用法マスタを登録する。
     */
    public void putAdminMaster(Collection c) {
        
        if (c == null) {
            return;
        }
        
        Iterator iter = c.iterator();
        while(iter.hasNext()) {
            AdminValue value = (AdminValue)iter.next();
            em.persist(value);
        }
    }
    
    /**
     * 用法コメントマスタを登録する。
     */
    public void putAdminComentMaster(Collection c) {
        
        if (c == null) {
            return;
        }
        
        Iterator iter = c.iterator();
        while(iter.hasNext()) {
            AdminComentValue value = (AdminComentValue)iter.next();
            em.persist(value);
        }
    }
    
    /**
     * 放射線メソッドマスタを登録する。
     */
    public void putRadMethodMaster(Collection c) {
        
        if (c == null) {
            return;
        }
        
        Iterator iter = c.iterator();
        while(iter.hasNext()) {
            RadiologyMethodValue value = (RadiologyMethodValue)iter.next();
            em.persist(value);
        }
    }
}
