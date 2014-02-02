package open.dolphin.order;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import open.dolphin.client.ClientContext;
import open.dolphin.client.MasterRenderer;
import open.dolphin.infomodel.DiseaseEntry;
import open.dolphin.table.ObjectTableModel;

public class DiagnosisMaster extends MasterPanel {

    private static final long serialVersionUID = -8731279062992813732L;
    private static final String[] diseaseColumns = ClientContext.getStringArray("masterSearch.disease.columnNames");
    private static final String codeSystem = ClientContext.getString("mml.codeSystem.diseaseMaster");
    private static final String[] sortButtonNames = ClientContext.getStringArray("masterSearch.disease.sortButtonNames");
    private static final String[] sortColumnNames = ClientContext.getStringArray("masterSearch.disease.sortColumnNames");

    /** 修飾語フィールド */
    //private JTextField modifierField;
    public DiagnosisMaster(String master) {
        super(master);
    }

    protected void initialize() {

        ButtonGroup bg = new ButtonGroup();
        sortButtons = new JRadioButton[sortButtonNames.length];
        for (int i = 0; i < sortButtonNames.length; i++) {
            JRadioButton radio = new JRadioButton(sortButtonNames[i]);
            sortButtons[i] = radio;
            bg.add(radio);
            radio.addActionListener(new SortActionListener(this, sortColumnNames[i], i));
        }

        int index = prefs.getInt("masterSearch.disease.sort", 0);
        sortButtons[index].setSelected(true);
        setSortBy(sortColumnNames[index]);

        tableModel = new ObjectTableModel(diseaseColumns, START_NUM_ROWS) {

            @Override
            public Class getColumnClass(int col) {
                return DiseaseEntry.class;
            }
        };

        // Table を生成する
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 行クリック処理を登録する
        ListSelectionModel m = table.getSelectionModel();
        m.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int row = table.getSelectedRow();
                    DiseaseEntry o = (DiseaseEntry) tableModel.getObject(row);
                    if (o != null) {
                        // Event adapter
                        MasterItem mItem = new MasterItem();
                        mItem.setClassCode(0);
                        mItem.setCode(o.getCode());
                        mItem.setName(o.getName());
                        mItem.setClaimDiseaseCode(mItem.getCode());
                        mItem.setMasterTableId(codeSystem);
                        setSelectedItem(mItem);
                    }
                }
            }
        });

        // 行選択を可能にする
        table.setRowSelectionAllowed(true);

        // カラム幅を設定する
        TableColumn column = null;
        int[] width = new int[]{50, 200, 200, 40, 50};
        int len = width.length;
        for (int i = 0; i < len; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(width[i]);
        }

        // レンダラを登録する
        DiseaseMasterRenderer dr = new DiseaseMasterRenderer();
        dr.setBeforStartColor(masterColors[0]);
        dr.setInUseColor(masterColors[1]);
        dr.setAfterEndColor(masterColors[2]);
        table.setDefaultRenderer(DiseaseEntry.class, dr);

        // Layout
        // Keyword
        JPanel key = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 5));
        key.add(findLabel);
        key.add(new JLabel("傷病名/修飾語:"));
        key.add(keywordField);
        key.setBorder(BorderFactory.createTitledBorder(keywordBorderTitle));

        JPanel sort = new JPanel();
        sort.setLayout(new BoxLayout(sort, BoxLayout.X_AXIS));
        for (int i = 0; i < sortButtons.length; i++) {
            if (i != 0) {
                sort.add(Box.createHorizontalStrut(5));
            }
            sort.add(sortButtons[i]);
        }
        sort.setBorder(BorderFactory.createTitledBorder("ソート"));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.add(key);
        top.add(Box.createHorizontalGlue());
        top.add(sort);

        // Table
        JScrollPane scroller = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.setLayout(new BorderLayout(0, 11));
        this.add(top, BorderLayout.NORTH);
        this.add(scroller, BorderLayout.CENTER);
    }

    /**
     * 病名マスタ Table のレンダラー
     */
    protected final class DiseaseMasterRenderer extends MasterRenderer {

        private static final long serialVersionUID = -5209120802971568080L;
        private final int CODE_COLUMN = 0;
        private final int NAME_COLUMN = 1;
        private final int KANA_COLUMN = 2;
        private final int ICD10_COLUMN = 3;
        private final int DISUSES_COLUMN = 4;

        public DiseaseMasterRenderer() {
        }

        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {
            Component c = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    isFocused,
                    row, col);

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {

                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }


            JLabel label = (JLabel) c;

            if (value != null && value instanceof DiseaseEntry) {

                DiseaseEntry entry = (DiseaseEntry) value;

                String disUseDate = entry.getDisUseDate();

                setColor(label, disUseDate);

                switch (col) {

                    case CODE_COLUMN:
                        label.setText(entry.getCode());
                        break;

                    case NAME_COLUMN:
                        label.setText(entry.getName());
                        break;

                    case KANA_COLUMN:
                        label.setText(entry.getKana());
                        break;

                    case ICD10_COLUMN:
                        label.setText(entry.getIcdTen());
                        break;

                    case DISUSES_COLUMN:
                        if (disUseDate.startsWith("9")) {
                            label.setText("");
                        } else {
                            label.setText(disUseDate);
                        }
                        break;
                }

            } else {
                label.setBackground(Color.white);
                label.setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }
}
