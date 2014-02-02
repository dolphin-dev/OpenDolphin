package open.dolphin.client;

/**
 * AgreementModel
 * 
 * @author Kazushi Minagawa
 *
 */
public class AgreementModel {
	
	private String agreeText;
	
	private boolean agree;

	public boolean isAgree() {
		return agree;
	}

	public void setAgree(boolean agree) {
		this.agree = agree;
	}

	public String getAgreeText() {
		return agreeText;
	}

	public void setAgreeText(String agreeText) {
		this.agreeText = agreeText;
	}
}
