package open.dolphin.ejb;

import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import javax.persistence.PersistenceException;
import open.dolphin.infomodel.DgOid;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserModel;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

@Stateless
@SecurityDomain("openDolphinSysAd")
@RolesAllowed("sysAd")
@Remote({RemoteSystemService.class})
@RemoteBinding(jndiBinding="openDolphin/RemoteSystemService")
public class RemoteSystemServiceImpl extends DolphinService implements RemoteSystemService {
    
    @PersistenceContext
    private EntityManager em;
    
    /**
     * 通信を確認する。
     * @return Hello, OpenDolphin文字列
     */
    @Override
    public String helloDolphin() {
        return "Hello, OpenDolphin";
    }
    
    /**
     * 施設と管理者情報を登録する。
     *
     * @param user 施設管理者
     */
    @Override
    public void addFacilityAdmin(UserModel user) {

        // mail address
        String email = user.getEmail();
        if (email == null) {
            throw new PersistenceException("電子メールアドレスが空のため登録できません。");
        }
        
        // 施設IDに使用する OID を取得する
        DgOid oid = (DgOid)em.find(DgOid.class, new Long(1L));
        String baseOid = oid.getBaseOid();
        int nextNumber = oid.getNextNumber();
        oid.setNextNumber(nextNumber+1);
        StringBuilder sb = new StringBuilder();
        sb.append(baseOid);
        sb.append(".");
        sb.append(String.valueOf(nextNumber));
        String facilityId = sb.toString();
        
        // OIDをセットし施設レコードを生成する
        FacilityModel facility = user.getFacilityModel();
        facility.setFacilityId(facilityId);
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
        sb = new StringBuilder();
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
}
