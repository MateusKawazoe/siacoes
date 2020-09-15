package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;

public class BugReportDAO {
	Connection conn = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	
	public BugReport findById(int id) throws SQLException{
		try(
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT bugreport.*, \"user\".name " + 
				"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
				"WHERE idBugReport = ?")
		){
			stmt.setInt(1, id);
			
			try(rs = stmt.executeQuery()){
				if(rs.next()){
					return this.loadObject(rs);
				}else{
					return null;
				}
			}catch(SQLException e){
				System.out.println(e);
			}		
		}catch(SQLException e){
			System.out.println(e);
		}
	}
	
	public List<BugReport> listAll() throws SQLException{
		try(
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT bugreport.*, \"user\".name " +
					"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
					"ORDER BY status, reportdate")
		){
			List<BugReport> list = new ArrayList<BugReport>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}catch(SQLException e){
			System.out.println(e);
		}
	}
	
	public int save(BugReport bug) throws SQLException{
		boolean insert = (bug.getIdBugReport() == 0);
		
		try(conn = ConnectionDAO.getInstance().getConnection()){		
			if(insert){
				try(
					stmt = conn.prepareStatement(
						"INSERT INTO bugreport(idUser, module, title, description, reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
					 	Statement.RETURN_GENERATED_KEYS
					 );
				){
					executeStmt(bug)
				}catch(SQLException e){
					System.out.println(e);
				}
			}else{
				try(
					stmt = conn.prepareStatement(
						"UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?"
					);
				){
					executeStmt(bug)
				}catch(SQLException e){
					System.out.println(e);
				}
			}
		}catch(SQLException e){
			System.out.println(e);
		}
	}
	
	public int executeStmt(BugReport bug) throws SQLException {
		stmt.setInt(1, bug.getUser().getIdUser());
		stmt.setInt(2, bug.getModule().getValue());
		stmt.setString(3, bug.getTitle());
		stmt.setString(4, bug.getDescription());
		stmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
		stmt.setInt(6, bug.getType().getValue());
		stmt.setInt(7, bug.getStatus().getValue());
		if(bug.getStatus() == BugStatus.REPORTED){
			stmt.setNull(8, Types.DATE);
		}else{
			stmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
		}
		stmt.setString(9, bug.getStatusDescription());
		
		if(!insert){
			stmt.setInt(10, bug.getIdBugReport());
		}
		
		stmt.execute();
		
		if(insert){
			try(rs = stmt.getGeneratedKeys()){
				if(rs.next()){
					bug.setIdBugReport(rs.getInt(1));
				}
			}catch(SQLException e){
				System.out.println(e)
			}		
		}
		
		return bug.getIdBugReport();
	}
	
	private BugReport loadObject(ResultSet rs) throws SQLException{
		BugReport bug = new BugReport();
		
		bug.setIdBugReport(rs.getInt("idBugReport"));
		bug.setUser(new User());
		bug.getUser().setIdUser(rs.getInt("idUser"));
		bug.getUser().setName(rs.getString("name"));
		bug.setModule(Module.SystemModule.valueOf(rs.getInt("module")));
		bug.setTitle(rs.getString("title"));
		bug.setDescription(rs.getString("description"));
		bug.setReportDate(rs.getDate("reportDate"));
		bug.setType(BugReport.BugType.valueOf(rs.getInt("type")));
		bug.setStatus(BugReport.BugStatus.valueOf(rs.getInt("status")));
		bug.setStatusDate(rs.getDate("statusDate"));
		bug.setStatusDescription(rs.getString("statusDescription"));
		
		return bug;
	}

}
