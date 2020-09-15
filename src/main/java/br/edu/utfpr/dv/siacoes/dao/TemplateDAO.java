package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class TemplateDAO<aux> {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    public List<aux> listAll(String query) throws SQLException {
        try(
            conn = ConnectionDAO.getInstance().getConnection()
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query)
        ){		
			List<aux> list = new ArrayList<aux>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}catch(SQLException e){
			System.out.println(e);
        }
    }

    public abstract aux findById(int id) throws SQLException;
    public abstract aux loadObject(ResultSet rs) throws SQLException;
}