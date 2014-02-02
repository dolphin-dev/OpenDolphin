package open.dolphin.session;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AppointmentModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Named
@Stateless
public class AppoServiceBean {

    private static final String QUERY_APPOINTMENT_BY_KARTE_ID = "from AppointmentModel a where a.karte.id=:karteId and a.date between :fromDate and :toDate";
    private static final String KARTE_ID = "karteId";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";

    @PersistenceContext
    private EntityManager em;


    public int putAppointments(List<AppointmentModel> list) {

        int cnt = 0;

        for (AppointmentModel model : list) {

            int state = model.getState();
            String appoName = model.getName();

            if (state == AppointmentModel.TT_NEW) {
                // 新規予約
                 em.persist(model);
                cnt++;

            } else if (state == AppointmentModel.TT_REPLACE && appoName != null) {
                // 変更された予約
                em.merge(model);
                cnt++;

            } else if (state == AppointmentModel.TT_REPLACE && appoName == null) {
                // 取り消された予約
                AppointmentModel target = (AppointmentModel)em.find(AppointmentModel.class, model.getId());
                em.remove(target);
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * 予約を検索する。
     * @param spec 検索仕様
     * @return 予約の Collection
     */
    public List<List> getAppointmentList(long karteId, List fromDate, List toDate) {

        // 抽出期間は別けられている
        int len = fromDate.size();
        List<List> ret = new ArrayList<List>(len);

        // 抽出期間ごとに検索しコレクションに加える
        for (int i = 0; i < len; i++) {

            List c = em.createQuery(QUERY_APPOINTMENT_BY_KARTE_ID)
            .setParameter(KARTE_ID, karteId)
            .setParameter(FROM_DATE, fromDate.get(i))
            .setParameter(TO_DATE, toDate.get(i))
            .getResultList();
            ret.add(c);
        }

        return ret;
    }
}
