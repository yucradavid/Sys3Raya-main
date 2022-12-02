/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.upeu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.edu.upeu.Coon.ConnS;
import pe.edu.upeu.modelo.ResultadoTO;

/**
 *
 * @author LENOVO
 */
public class ResultadoDao implements ResultadoDaoI {

    ConnS intance = ConnS.getInstance();
    Connection conexion = intance.getConnection();
    PreparedStatement ps;
    ResultSet rs;

    @Override
    public List<ResultadoTO> listarResultados() {
        List<ResultadoTO> listarResultado = new ArrayList();
        String sql = "SELECT * FROM  resultado";
   //where
        try {           
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
            //System.out.println("OTRO"+rs.getString("id_resultado"));    

                ResultadoTO cli = new ResultadoTO();
               //cli.setIdResultado(rs.getInt("id_resultado"));
                cli.setNombrepartida(rs.getString("nombre_partida"));
                cli.setNombrejugador1(rs.getString("nombre_jugador1"));
                cli.setNombrejugador2(rs.getString("nombre_jugador2"));
                cli.setGanador(rs.getString("ganador"));
                cli.setPunto(rs.getInt("punto"));
                cli.setEstado(rs.getString("estado"));
                listarResultado.add(cli);
            }

        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return listarResultado;
    }

    @Override
    public int crearResultado(ResultadoTO re) {
       
        int exec = 0;
        int i = 0;
        String sql = "INSERT INTO resultado(nombre_partida, nombre_jugador1, nombre_jugador2, ganador, punto, estado)"
                + " values(?,?,?,?,?,?)";
        try {
            ps = conexion.prepareStatement(sql); //tiene que tener 4 parametros de entrada
            //ps.setInt(++i, re.getIdResultado());
            ps.setString(++i, re.getNombrepartida());
            ps.setString(++i, re.getNombrejugador1());
            ps.setString(++i, re.getNombrejugador2());
            ps.setString(++i, re.getGanador());
            ps.setInt(++i, re.getPunto());
            ps.setString(++i, re.getEstado());
           
            exec = ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("create:" + ex.toString());

        }
        return exec;

    }

    @Override
    public int update(ResultadoTO d) {
         System.out.println("actualizar d.nombre_partida: " + d.getNombrepartida());
        int comit = 0;
        
        String sql = "UPDATE resultado  "              
                + "nombre_partida=?, "
                + "nombre_jugador1=?, "
                + "nombre_jugador2=?, "
                + "ganador=?, "
                + "punto=?, "
                + "estado=? "
                + "WHERE nombre_partida=?";
        int i = 0;
        
        try {
            ps = conexion.prepareStatement(sql);
            ps.setString(++i, d.getNombrepartida());
            ps.setString(++i, d.getNombrejugador1());
            ps.setString(++i, d.getNombrejugador2());
            
            ps.setString(++i, d.getGanador());
            ps.setInt(++i, d.getPunto());
            ps.setString(++i, d.getEstado());
            
            comit = ps.executeUpdate();
        } catch (SQLException ex) {
        
        }
        return comit;
    }

    @Override
        public int delete(int id){
        int idx=0;
        int i=0;
        String sql="delete from resultado where nombre_partida=?";
        try {
             ps=conexion.prepareStatement(sql);
             ps.setInt(++i, id);
             idx=ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error :"+e.getMessage());
        }        
        return idx;
    }
    }

    

    
    


             