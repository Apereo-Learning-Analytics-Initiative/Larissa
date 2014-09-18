package nl.uva.larissa.json.model;

import javax.validation.Valid;

import org.apache.abdera.i18n.iri.IRI;

import nl.uva.larissa.json.model.validate.ValidIFI;
import nl.uva.larissa.json.model.validate.MailToIRI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

// explicit NON_NULL as this is used indirectly by AgentQuery in a ComplexKey
@JsonInclude(Include.NON_NULL)
@ValidIFI
public class IFI {
	@MailToIRI
	IRI mbox;
	String mbox_sha1sum;
	@JsonProperty("openid")
	String openID;
	@Valid
	Account account;

	public IRI getMbox() {
		return mbox;
	}

	public void setMbox(IRI mbox) {
		this.mbox = mbox;
	}

	public String getMbox_sha1sum() {
		return mbox_sha1sum;
	}

	public void setMbox_sha1sum(String mbox_sha1sum) {
		this.mbox_sha1sum = mbox_sha1sum;
	}

	public String getOpenID() {
		return openID;
	}

	public void setOpenID(String openID) {
		this.openID = openID;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
