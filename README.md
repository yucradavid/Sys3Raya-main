# Sys3Raya-main
Examen de C2-G2 Crear un juego de nombre 3 en raya con una base de datos.
# Video Explicando "DRIVE" Duracion - 20 min.
# Presentación
Le presento mi aplicación con el cual se puede realizar un juego de 3 en raya.
Es un aplicación que utiliza algunos patrones de diseño, se introdujo las siguientes Clases de java.
# ConnS 
Esta clase se utiliza para llamar a la base de datos y poder exportarlo a otras clases.

```console
public class ConnS {
    

    private static volatile ConnS instance;
    private static volatile Connection connection;

    private ConnS() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
            //Logger.getLogger(ConnS.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to create");
        }
        if (connection != null) {
            throw new RuntimeException("Use getConnection() method to create");
        }
    }

    public static ConnS getInstance() {
        if (instance == null) {
            synchronized (ConnS.class) {
                if (instance == null) {
                    instance = new ConnS();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        if (connection == null) {
            synchronized (ConnS.class) {
                if (connection == null) {
                    try {
                        String dbUrl = "jdbc:sqlite:data/db_juego.db?foreign_keys=on;";
                        connection = DriverManager.getConnection(dbUrl);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return connection;
    }
}
```
# ResultadoDao
Se utiliza esta clase se utiliza para poder listar, crear, eliminar, actualizar, yo solo implemente estos 4 tipos de public class
codigo::
```console
package pe.edu.upeu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.edu.upeu.Coon.ConnS;
import pe.edu.upeu.modelo.ResultadoTO;

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
```
# ResultadoDaoI
En esta clase se muestra los class, onde se pondrán las clases y eso se ira a ResultadoDao
```console
import java.util.List;
import pe.edu.upeu.modelo.ResultadoTO;

public interface ResultadoDaoI {

  
    public List<ResultadoTO> listarResultados();
    public int update(ResultadoTO d);
    public int delete(int id);
    public int crearResultado(ResultadoTO re);
}
```
# GameGui
En esta class se pondra el juego que consta con JButton que en esta class se dara sus acciones como crear, agregar a la lista.
```console
public class GameGui {

    private MainJuego ventana;
    private ModeloGame modelo;
    private JButton[][] MatrizButton;

    public GameGui(MainJuego ventana, ModeloGame modelo) {
        this.ventana = ventana;
        this.modelo = modelo;
        MatrizButton = this.ventana.getMatrizButton();
        agregarListeners();
        crearPlayer();
    }

    private void agregarListeners() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                agregarEventoMouse(i, j);
            }
        }
        JButton Anular=ventana.getAnular();
        Anular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                modelo.resetJP(MatrizButton);
                      
        }
     });
    }

    private void agregarEventoMouse(int i, int j) {
        JButton ABtn = MatrizButton[i][j];
        ABtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                modelo.marcarBtn(i,j,MatrizButton,ABtn);
        }
     });
    
    }
     private void crearPlayer() {
        JLabel j1 =ventana.getVitoriasJ1();
        JLabel j2 =ventana.getVitoriasJ2();
        modelo.setPlayer(j1,j2);
    }


}
```
# MainJuego
Es donde Va estar el listarResultado y el crear que estará en la acción del JButton,  en donde se encuentra el grafico donde se pondrá las acciones correspondientes en la aplicación.

Se encuentran los JButton de los cuales se les pondra cciones.
```console
public class MainJuego extends javax.swing.JFrame {

    ResultadoDaoI cDao;
    DefaultTableModel modelo;
    ResultadoDao resul;
   
    private JButton[][] MatrizButton;
    

    public MainJuego() {
        MatrizButton = new JButton[3][3];
        initComponents();
        AsignarButtons();
        ListarResultado();

    }

    public List<ResultadoTO> ListarResultado() {
        cDao = new ResultadoDao();
        List<ResultadoTO> listarResultados = cDao.listarResultados();
        jTable1.setAutoCreateRowSorter(true);
        modelo = (DefaultTableModel) jTable1.getModel();

        Object[] ob = new Object[6];
        for (int i = 0; i < listarResultados.size(); i++) {
            //ob[0] = i + 1;
           
            ob[0] = listarResultados.get(i).getNombrepartida();
            ob[1] = listarResultados.get(i).getNombrejugador1();
            ob[2] = listarResultados.get(i).getNombrejugador2();
            ob[3] = listarResultados.get(i).getGanador();
            ob[4] = listarResultados.get(i).getPunto();
            ob[5] = listarResultados.get(i).getEstado();

            modelo.addRow(ob);
        }
        System.out.println("hola" + modelo.getColumnName(1));
        jTable1.setModel(modelo);

        return listarResultados;

    }
    private void AsignarButtons() {
        MatrizButton[0][0] = boton1;
        MatrizButton[0][1] = boton2;
        MatrizButton[0][2] = boton3;
        MatrizButton[1][0] = boton4;
        MatrizButton[1][1] = boton5;
        MatrizButton[1][2] = boton6;
        MatrizButton[2][0] = boton7;
        MatrizButton[2][1] = boton8;
        MatrizButton[2][2] = boton9;
    }

    public JButton[][] getMatrizButton() {
        return MatrizButton;
    }

    public JLabel getVitoriasJ1() {
        
        return VictoriasJ1;
    }

    public JLabel getVitoriasJ2() {
        return VictoriasJ2;
        
    }

    public JButton getAnular() {
        return Anular;
    }

  
    private void BtnIniciarActionPerformed(java.awt.event.ActionEvent evt) {                                           

        ListarResultado();
        cDao = new ResultadoDao();
        ResultadoTO to = new ResultadoTO();
        to.setIdResultado(0);
        to.setNombrepartida("partido");
        to.setNombrejugador1(txtNombreJugador1.getText());
        to.setNombrejugador2(txtNombreJugador2.getText());
        to.setGanador("");
        to.setPunto(0);
        to.setEstado("");
        //if(getEstado.jTable1=("finalizado"));
        //else evt= getRstado("terminado")
        cDao.crearResultado(to);

        jTable1.setModel(modelo);
        System.out.println("jugador1" + modelo.getColumnName(2));
        
        boton1.setEnabled(true);
        boton2.setEnabled(true);
        boton3.setEnabled(true);
        boton4.setEnabled(true);
        boton5.setEnabled(true);
        boton6.setEnabled(true);
        boton7.setEnabled(true);
        boton8.setEnabled(true);
        boton9.setEnabled(true);
```
# ModeloGame
En esta funcion se dara acciones a los JButton designadoles columnas y filas para que el jugador cuadno termine el juego vea quien gano y de que filas son los resultados.
x
o
se dara accion a que se puedan cambiar automaticamente.
```console
public class ModeloGame {

    MainJuego Gp;
    ResultadoDaoI cDao;
    DefaultTableModel modelo;
    private String turno;
    private boolean end;
    private boolean empate;
    public JLabel Xj1;
    public JLabel Oj2;
    private String[][] tablero;
    private int cantJugadas;
    private int victoriasJ1;
    private int victoriasJ2;

    public ModeloGame() {

        turno = "X";
        end = false;
        empate = false;
        tablero = new String[3][3];
        cantJugadas = 0;
        victoriasJ1 = 0;
        victoriasJ2 = 0;

    }

    public void marcarBtn(int i, int j, JButton[][] MatrizButton, javax.swing.JButton bt) {
        if (!end) {
            if (tablero[i][j] == null) {
                tablero[i][j] = turno;
                MatrizButton[i][j].setText(turno);
                cantJugadas++;
                verificarEstado();
                if (!end) {
                    if (turno.equals("X")) {
                        turno = "O";
                        bt.setForeground(Color.red);
                        bt.setFont(new Font("Ink Free", Font.BOLD, 56));
                    } else {
                        turno = "X";
                        bt.setForeground(Color.blue);
                        bt.setFont(new Font("Ink Free", Font.BOLD, 56));
                    }
                } else {
                    terminarJuego();
                }
            }
        }
    }

    private void verificarEstado() {
        verificarFilas();
        if (!end) {
            verificarColumnas();
            if (!end) {
                verificarDiagonalP();
                if (!end) {
                    verificarDiagonalS();
                    if (cantJugadas == 9) {
                        empate = true;
                        end = true;
                    }
                }
            }
        }
    }

    private void verificarFilas() {
        for (int i = 0; i < 3 && !end; i++) {
            boolean win = true;
            for (int j = 0; j < 3 && win; j++) {
                if (tablero[i][j] == null || !tablero[i][j].equals(turno)) {
                    win = false;
                }
            }
            if (win) {
                end = true;
            }
        }
    }

    private void verificarColumnas() {
        for (int j = 0; j < 3 && !end; j++) {
            boolean win = true;
            for (int i = 0; i < 3 && win; i++) {
                if (tablero[i][j] == null || !tablero[i][j].equals(turno)) {
                    win = false;
                }
            }
            if (win) {
                end = true;
            }
        }
    }

    private void verificarDiagonalP() {

        boolean win = true;
        for (int i = 0; i < 3 && win; i++) {
            if (tablero[i][i] == null || !tablero[i][i].equals(turno)) {
                win = false;
            }
        }
        if (win) {
            end = true;
        }

    }

    private void verificarDiagonalS() {

        boolean win = true;
        int j = 2;
        for (int i = 0; i < 3 && win; i++, j--) {
            if (tablero[i][j] == null || !tablero[i][j].equals(turno)) {
                win = false;
            }
        }
        if (win) {
            end = true;
        }

    }

    private void terminarJuego() {

        if (empate) {
            victoriasJ1 = 0;
            victoriasJ2 = 0;
            JOptionPane.showMessageDialog(null, "Empate");
            Gp.ListarResultado();
        } else {
            if (turno.equals("X")) {
                victoriasJ1++;
                Xj1.setText(String.valueOf(victoriasJ1));
                JOptionPane.showMessageDialog(null, "Victoria de jugador 1");
                Gp.ListarResultado();

                //clasegu
            } else {
                victoriasJ2++;
                Oj2.setText(String.valueOf(victoriasJ2));
                JOptionPane.showMessageDialog(null, "Victoria del jugador 2");
                Gp.ListarResultado();
                //clase
            }
        }
    }

    public void setPlayer(JLabel j1, JLabel j2) {
        Xj1 = j1;
        Oj2 = j2;
    }

    public void resetJP(JButton[][] MatrizButton) {
        turno = "X";
        end = false;
        empate = false;
        cantJugadas = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = null;
                MatrizButton[i][j].setText("");

            }

        }
    }

}
```
#ResultadoTO
En este class se colocaran las partes que tiene tu base de datos , pero llamdo a los mismos nombres de la base de datos y dopnde se importa las partes de la base de datos ejemplo: jugador1 jugador2 ... se colocaran deacuerdo a que tipo pertenecen pueden ser string o int.
```console
@Data
public class ResultadoTO {

    public int IdResultado, punto;
    public String nombrepartida, nombrejugador1, nombrejugador2, ganador, estado;

}
```
