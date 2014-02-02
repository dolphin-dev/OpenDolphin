package open.dolphin.infomodel;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Type;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_letter_text")
public class LetterText extends InfoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Lob
    @Type(type="org.hibernate.type.StringClobType")
    private String textValue;

    @ManyToOne
    @JoinColumn(name="module_id", nullable=false)
    private LetterModule module;

    public LetterText() {
    }

    public LetterText(String name, String textValue) {
        this();
        this.name = name;
        this.textValue = textValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LetterText)) {
            return false;
        }
        LetterText other = (LetterText) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "open.dolphin.infomodel.DocumentText[id=" + getId() + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public LetterModule getModule() {
        return module;
    }

    public void setModule(LetterModule module) {
        this.module = module;
    }
}
