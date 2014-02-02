package open.dolphin.infomodel;

import java.util.Collection;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

/**
 * 瀬田クリニック版初診時情報2
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="docType",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("SETA_2")
public class FirstEncounter2Model extends FirstEncounterModel implements java.io.Serializable {
    
    @Transient
    private String prevCare;
    
    @Transient
    private String surgeryDate1;
    
    @Transient
    private String surgery1;
    
    @Transient
    private String surgeryDate2;
    
    @Transient
    private String surgery2;
    
    @Transient
    private String surgeryDate3;
    
    @Transient
    private String surgery3;
    
    @Transient
    private String radDate1;
    
    @Transient
    private String radDate2;
    
    @Transient
    private String radDate3;
    
    @Transient
    private String rad1;
    
    @Transient
    private String rad2;
    
    @Transient
    private String rad3;
    
    @Transient
    private String otherDate1;
    
    @Transient
    private String otherDate2;
    
    @Transient
    private String otherDate3;
    
    @Transient
    private String otherDate4;
    
    @Transient
    private String otherDate5;
    
    @Transient
    private String other1;
    
    @Transient
    private String other2;
    
    @Transient
    private String other3;
    
    @Transient
    private String other4;
    
    @Transient
    private String other5;
    
    @Transient
    private String chemicalFrom1;
    
    @Transient
    private String chemicalTo1;
    
    @Transient
    private String chemotherapy1;

    @Transient
    private String chemicalFrom2;
    
    @Transient
    private String chemicalTo2;
    
    @Transient
    private String chemotherapy2;

    @Transient
    private String chemicalFrom3;
    
    @Transient
    private String chemicalTo3;
    
    @Transient
    private String chemotherapy3;

    @Transient
    private String chemicalFrom4;
    
    @Transient
    private String chemicalTo4;
    
    @Transient
    private String chemotherapy4;

    @Transient
    private String chemicalFrom5;
    
    @Transient
    private String chemicalTo5;
    
    @Transient
    private String chemotherapy5;

    @Transient
    private String chemicalFrom6;
    
    @Transient
    private String chemicalTo6;
    
    @Transient
    private String chemotherapy6;
    
    @Transient
    private String evalSpecimen;
    
    @Transient
    private String satueiDate1;
    
    @Transient
    private String location1;
    
    @Transient
    private String imageType1;
        
    @Transient
    private String otherImage1;
    
    @Transient
    private String satueiDate2;
    
    @Transient
    private String location2;
    
    @Transient
    private String imageType2;
        
    @Transient
    private String otherImage2;
    
    @Transient
    private String genpatuType;
       
    @Transient
    private String gennpatuSize1;

    @Transient
    private String gennpatuSize2;
    
    @Transient
    private String tazokiType;
       
    @Transient
    private String tazokiSize1;

    @Transient
    private String tazokiSize2;

    @Transient
    private String lymphType;

    @Transient
    private String lymphSize1;

    @Transient
    private String lymphSize2;
    
    @Transient
    private String noneTarget;
    
    @Transient
    private String mesuableSum;
        
    @Transient
    private String unmesuableType;

    @Transient
    private String unmesuableNote;
    
    @Transient
    private String imageObjectiveNotes;
        
    @Transient
    private String tokkiJiko;
    
    @Transient
    private String heiyoType;
        
    @Transient
    private String heiyoChiryo;

    @Transient
    private String carePolicy;

    @Transient
    private String physioObjectiveNotes;
    
    @Transient
    private Collection compositeImages;
    
    
    /** Creates a new instance of FirstEncounter2 */
    public FirstEncounter2Model() {
    }

    public String getPrevCare() {
        return prevCare;
    }

    public void setPrevCare(String prevCare) {
        this.prevCare = prevCare;
    }

    public String getSurgeryDate1() {
        return surgeryDate1;
    }

    public void setSurgeryDate1(String surgeryDate1) {
        this.surgeryDate1 = surgeryDate1;
    }

    public String getSurgery1() {
        return surgery1;
    }

    public void setSurgery1(String surgery1) {
        this.surgery1 = surgery1;
    }

    public String getSurgeryDate2() {
        return surgeryDate2;
    }

    public void setSurgeryDate2(String surgeryDate2) {
        this.surgeryDate2 = surgeryDate2;
    }

    public String getSurgery2() {
        return surgery2;
    }

    public void setSurgery2(String surgery2) {
        this.surgery2 = surgery2;
    }

    public String getSurgeryDate3() {
        return surgeryDate3;
    }

    public void setSurgeryDate3(String surgeryDate3) {
        this.surgeryDate3 = surgeryDate3;
    }

    public String getSurgery3() {
        return surgery3;
    }

    public void setSurgery3(String surgery3) {
        this.surgery3 = surgery3;
    }

    public String getRadDate1() {
        return radDate1;
    }

    public void setRadDate1(String radDate1) {
        this.radDate1 = radDate1;
    }

    public String getRadDate2() {
        return radDate2;
    }

    public void setRadDate2(String radDate2) {
        this.radDate2 = radDate2;
    }

    public String getRadDate3() {
        return radDate3;
    }

    public void setRadDate3(String radDate3) {
        this.radDate3 = radDate3;
    }

    public String getRad1() {
        return rad1;
    }

    public void setRad1(String rad1) {
        this.rad1 = rad1;
    }

    public String getRad2() {
        return rad2;
    }

    public void setRad2(String rad2) {
        this.rad2 = rad2;
    }

    public String getRad3() {
        return rad3;
    }

    public void setRad3(String rad3) {
        this.rad3 = rad3;
    }

    public String getOtherDate1() {
        return otherDate1;
    }

    public void setOtherDate1(String otherDate1) {
        this.otherDate1 = otherDate1;
    }

    public String getOtherDate2() {
        return otherDate2;
    }

    public void setOtherDate2(String otherDate2) {
        this.otherDate2 = otherDate2;
    }

    public String getOtherDate3() {
        return otherDate3;
    }

    public void setOtherDate3(String otherDate3) {
        this.otherDate3 = otherDate3;
    }

    public String getOtherDate4() {
        return otherDate4;
    }

    public void setOtherDate4(String otherDate4) {
        this.otherDate4 = otherDate4;
    }

    public String getOtherDate5() {
        return otherDate5;
    }

    public void setOtherDate5(String otherDate5) {
        this.otherDate5 = otherDate5;
    }

    public String getOther1() {
        return other1;
    }

    public void setOther1(String other1) {
        this.other1 = other1;
    }

    public String getOther2() {
        return other2;
    }

    public void setOther2(String other2) {
        this.other2 = other2;
    }

    public String getOther3() {
        return other3;
    }

    public void setOther3(String other3) {
        this.other3 = other3;
    }

    public String getOther4() {
        return other4;
    }

    public void setOther4(String other4) {
        this.other4 = other4;
    }

    public String getOther5() {
        return other5;
    }

    public void setOther5(String other5) {
        this.other5 = other5;
    }

    public String getChemicalFrom1() {
        return chemicalFrom1;
    }

    public void setChemicalFrom1(String chemicalFrom1) {
        this.chemicalFrom1 = chemicalFrom1;
    }

    public String getChemicalTo1() {
        return chemicalTo1;
    }

    public void setChemicalTo1(String chemicalTo1) {
        this.chemicalTo1 = chemicalTo1;
    }

    public String getChemotherapy1() {
        return chemotherapy1;
    }

    public void setChemotherapy1(String chemotherapy1) {
        this.chemotherapy1 = chemotherapy1;
    }

    public String getChemicalFrom2() {
        return chemicalFrom2;
    }

    public void setChemicalFrom2(String chemicalFrom2) {
        this.chemicalFrom2 = chemicalFrom2;
    }

    public String getChemicalTo2() {
        return chemicalTo2;
    }

    public void setChemicalTo2(String chemicalTo2) {
        this.chemicalTo2 = chemicalTo2;
    }

    public String getChemotherapy2() {
        return chemotherapy2;
    }

    public void setChemotherapy2(String chemotherapy2) {
        this.chemotherapy2 = chemotherapy2;
    }

    public String getChemicalFrom3() {
        return chemicalFrom3;
    }

    public void setChemicalFrom3(String chemicalFrom3) {
        this.chemicalFrom3 = chemicalFrom3;
    }

    public String getChemicalTo3() {
        return chemicalTo3;
    }

    public void setChemicalTo3(String chemicalTo3) {
        this.chemicalTo3 = chemicalTo3;
    }

    public String getChemotherapy3() {
        return chemotherapy3;
    }

    public void setChemotherapy3(String chemotherapy3) {
        this.chemotherapy3 = chemotherapy3;
    }

    public String getChemicalFrom4() {
        return chemicalFrom4;
    }

    public void setChemicalFrom4(String chemicalFrom4) {
        this.chemicalFrom4 = chemicalFrom4;
    }

    public String getChemicalTo4() {
        return chemicalTo4;
    }

    public void setChemicalTo4(String chemicalTo4) {
        this.chemicalTo4 = chemicalTo4;
    }

    public String getChemotherapy4() {
        return chemotherapy4;
    }

    public void setChemotherapy4(String chemotherapy4) {
        this.chemotherapy4 = chemotherapy4;
    }

    public String getChemicalFrom5() {
        return chemicalFrom5;
    }

    public void setChemicalFrom5(String chemicalFrom5) {
        this.chemicalFrom5 = chemicalFrom5;
    }

    public String getChemicalTo5() {
        return chemicalTo5;
    }

    public void setChemicalTo5(String chemicalTo5) {
        this.chemicalTo5 = chemicalTo5;
    }

    public String getChemotherapy5() {
        return chemotherapy5;
    }

    public void setChemotherapy5(String chemotherapy5) {
        this.chemotherapy5 = chemotherapy5;
    }

    public String getChemicalFrom6() {
        return chemicalFrom6;
    }

    public void setChemicalFrom6(String chemicalFrom6) {
        this.chemicalFrom6 = chemicalFrom6;
    }

    public String getChemicalTo6() {
        return chemicalTo6;
    }

    public void setChemicalTo6(String chemicalTo6) {
        this.chemicalTo6 = chemicalTo6;
    }

    public String getChemotherapy6() {
        return chemotherapy6;
    }

    public void setChemotherapy6(String chemotherapy6) {
        this.chemotherapy6 = chemotherapy6;
    }

    public String getEvalSpecimen() {
        return evalSpecimen;
    }

    public void setEvalSpecimen(String evalSpecimen) {
        this.evalSpecimen = evalSpecimen;
    }

    public String getSatueiDate1() {
        return satueiDate1;
    }

    public void setSatueiDate1(String satueiDate1) {
        this.satueiDate1 = satueiDate1;
    }

    public String getLocation1() {
        return location1;
    }

    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    public String getImageType1() {
        return imageType1;
    }

    public void setImageType1(String imageType1) {
        this.imageType1 = imageType1;
    }

    public String getOtherImage1() {
        return otherImage1;
    }

    public void setOtherImage1(String otherImage1) {
        this.otherImage1 = otherImage1;
    }

    public String getSatueiDate2() {
        return satueiDate2;
    }

    public void setSatueiDate2(String satueiDate2) {
        this.satueiDate2 = satueiDate2;
    }

    public String getLocation2() {
        return location2;
    }

    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    public String getImageType2() {
        return imageType2;
    }

    public void setImageType2(String imageType2) {
        this.imageType2 = imageType2;
    }

    public String getOtherImage2() {
        return otherImage2;
    }

    public void setOtherImage2(String otherImage2) {
        this.otherImage2 = otherImage2;
    }

    public String getGenpatuType() {
        return genpatuType;
    }

    public void setGenpatuType(String genpatuType) {
        this.genpatuType = genpatuType;
    }

    public String getGennpatuSize1() {
        return gennpatuSize1;
    }

    public void setGennpatuSize1(String gennpatuSize1) {
        this.gennpatuSize1 = gennpatuSize1;
    }

    public String getGennpatuSize2() {
        return gennpatuSize2;
    }

    public void setGennpatuSize2(String gennpatuSize2) {
        this.gennpatuSize2 = gennpatuSize2;
    }

    public String getTazokiType() {
        return tazokiType;
    }

    public void setTazokiType(String tazokiType) {
        this.tazokiType = tazokiType;
    }

    public String getTazokiSize1() {
        return tazokiSize1;
    }

    public void setTazokiSize1(String tazokiSize1) {
        this.tazokiSize1 = tazokiSize1;
    }

    public String getTazokiSize2() {
        return tazokiSize2;
    }

    public void setTazokiSize2(String tazokiSize2) {
        this.tazokiSize2 = tazokiSize2;
    }

    public String getLymphType() {
        return lymphType;
    }

    public void setLymphType(String lymphType) {
        this.lymphType = lymphType;
    }

    public String getLymphSize1() {
        return lymphSize1;
    }

    public void setLymphSize1(String lymphSize1) {
        this.lymphSize1 = lymphSize1;
    }

    public String getLymphSize2() {
        return lymphSize2;
    }

    public void setLymphSize2(String lymphSize2) {
        this.lymphSize2 = lymphSize2;
    }

    public String getNoneTarget() {
        return noneTarget;
    }

    public void setNoneTarget(String noneTarget) {
        this.noneTarget = noneTarget;
    }

    public String getMesuableSum() {
        return mesuableSum;
    }

    public void setMesuableSum(String mesuableSum) {
        this.mesuableSum = mesuableSum;
    }

    public String getUnmesuableType() {
        return unmesuableType;
    }

    public void setUnmesuableType(String unmesuableType) {
        this.unmesuableType = unmesuableType;
    }

    public String getUnmesuableNote() {
        return unmesuableNote;
    }

    public void setUnmesuableNote(String unmesuableNote) {
        this.unmesuableNote = unmesuableNote;
    }

    public String getImageObjectiveNotes() {
        return imageObjectiveNotes;
    }

    public void setImageObjectiveNotes(String imageObjectiveNotes) {
        this.imageObjectiveNotes = imageObjectiveNotes;
    }

    public String getTokkiJiko() {
        return tokkiJiko;
    }

    public void setTokkiJiko(String tokkiJiko) {
        this.tokkiJiko = tokkiJiko;
    }

    public String getHeiyoType() {
        return heiyoType;
    }

    public void setHeiyoType(String heiyoType) {
        this.heiyoType = heiyoType;
    }

    public String getHeiyoChiryo() {
        return heiyoChiryo;
    }

    public void setHeiyoChiryo(String heiyoChiryo) {
        this.heiyoChiryo = heiyoChiryo;
    }

    public String getCarePolicy() {
        return carePolicy;
    }

    public void setCarePolicy(String carePolicy) {
        this.carePolicy = carePolicy;
    }

    public String getPhysioObjectiveNotes() {
        return physioObjectiveNotes;
    }

    public void setPhysioObjectiveNotes(String physioObjectiveNotes) {
        this.physioObjectiveNotes = physioObjectiveNotes;
    }

    public Collection getCompositeImages() {
        return compositeImages;
    }

    public void setCompositeImages(Collection compositeImages) {
        this.compositeImages = compositeImages;
    }
}
