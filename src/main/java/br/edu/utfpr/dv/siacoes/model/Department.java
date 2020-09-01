package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import lombok.Data;

public @Data class Department implements Serializable {
	private static final long serialVersionUID = 1L;
	private int idDepartment;
	private Campus campus;
	private String name;
	private String fullName;
	private transient byte[] logo;
	private boolean active;
	private String site;
	private String initials;
}
