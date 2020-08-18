package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentDAO {
	Connection conn = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;

	public Department findById(int id) throws SQLException{
		String query = "SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?";
		
		return listExecuteQuery(query);
	}
	
	public List<Department> listAll(boolean onlyActive) throws SQLException{
		String query = "SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
				(onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name";
		
		return listExecuteQuery(query);
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			closeConnection(rs, stmt, conn);
		}
	}
	
	public int save(int idUser, Department department) throws SQLException{
		boolean insert = (department.getIdDepartment() == 0);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?");
			}
			
			stmt.setInt(1, department.getCampus().getIdCampus());
			stmt.setString(2, department.getName());
			if(department.getLogo() == null){
				stmt.setNull(3, Types.BINARY);
			}else{
				stmt.setBytes(3, department.getLogo());	
			}
			stmt.setInt(4, department.isActive() ? 1 : 0);
			stmt.setString(5, department.getSite());
			stmt.setString(6, department.getFullName());
			stmt.setString(7, department.getInitials());
			
			if(!insert){
				stmt.setInt(8, department.getIdDepartment());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					department.setIdDepartment(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, department);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, department);
			}
			
			return department.getIdDepartment();
		}finally{
			closeConnection(rs, stmt, conn);
		}
	}
	
	public List<Department> listExecuteQuery(String query) throws SQLException{
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery(query);
			
			List<Department> list = new ArrayList<Department>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}

			return list;
		} finally{
			closeConnection(rs, stmt, conn);
		}
	}
	
	private void closeConnection(ResultSet rs, Statement stmt, Connection conn) {
		if((rs != null) && !rs.isClosed())
			rs.close();
		if((stmt != null) && !stmt.isClosed())
			stmt.close();
		if((conn != null) && !conn.isClosed())
			conn.close();	
	}
	
	private Department loadObject(ResultSet rs) throws SQLException{
		Department department = new Department();
		
		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));
		
		return department;
	}
	
}
