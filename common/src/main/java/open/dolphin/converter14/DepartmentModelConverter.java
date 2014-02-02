package open.dolphin.converter14;

import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.IInfoModel;

/**
 * DepartmentModel
 *
 * @author Minagawa,Kazushi
 *
 */
public final class DepartmentModelConverter implements IInfoModelConverter {
   
    private DepartmentModel model;

    public DepartmentModelConverter() {
    }
    
    public String getDepartment() {
        return model.getDepartment();
    }

    public String getDepartmentDesc() {
        return model.getDepartmentDesc();
    }

    public String getDepartmentCodeSys() {
        return model.getDepartmentCodeSys();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (DepartmentModel)model;
    }
}
