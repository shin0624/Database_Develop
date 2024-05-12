import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JDBCtest {
	private static int currentPage = 1;//현재 페이지의 인덱스
	private static final int PAGE_SIZE = 3;//한 페이지 당 테이블의 개수
	private static Connection con = null;//데이터베이스 연결 객체
	public static JLabel info = new JLabel("학생 정보                                                                                                                     수강 정보                                                                                                                      과목 정보");
		

		
	   public static void main(String[] args) {
		   
	        try {
	            // 데이터베이스 연결 정보 설정
	            String url = "jdbc:oracle:thin:@localhost:1521:ORCL";
	            String user = "C##20214930";
	            String pwd = "Chosunapp7";

	            // 오라클 드라이버 로딩
	            Class.forName("oracle.jdbc.OracleDriver"); // oracle.jdbc.driver.OracleDriver가 아니라, 중간에 driver를 지워야 정상작동
	            System.out.println("Driver Loading Success!");
	            con = DriverManager.getConnection(url, user, pwd);
	            System.out.println("DB 연결 성공");
    
	            // GUI 생성
	            JFrame frame = new JFrame("DBProject_신범수");
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	            // 각 패널의 크기 조정
	            frame.setSize(1300, 600);

	            JPanel mainPanel = new JPanel(new BorderLayout());
	            mainPanel.add(info, BorderLayout.NORTH);
	          
	            JButton SELECT = new JButton("검색");
	            JButton ADD = new JButton("추가");
	            JButton DELETE = new JButton("삭제");
	            JButton UPDATE = new JButton("변경");
	           
	            JPanel buttonPanel = new JPanel();
	            
	            buttonPanel.add(SELECT);
	            buttonPanel.add(ADD);
	            buttonPanel.add(DELETE);
	            buttonPanel.add(UPDATE);
	            
	            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	            
	            
	            try (Statement stmt = con.createStatement()) {
//쿼리문자리
	            	
	                JPanel rightPanel = new JPanel(new GridLayout(1, PAGE_SIZE)); // 1행 4열의 그리드 레이아웃
	                rightPanel.setSize(600,600);
	              
	                mainPanel.add(rightPanel, BorderLayout.CENTER);
	                
	            
	                updateTables(rightPanel);//초기화면 테이블 로딩
	                
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }

	            frame.add(mainPanel);
	            frame.setVisible(true);

	        } catch (ClassNotFoundException e) {
	            System.out.println("드라이버 연동 실패");
	            e.printStackTrace();
	        } catch (SQLException e) {
	            System.out.println("오류 : 테이블이 이미 생성되어 있는지 확인하세요.");
	            e.printStackTrace();
	        } finally {
	            try {
	                if (con != null) {
	                    con.close();
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        
	 
	    }
	
	
	  // ResultSet을 기반으로 DefaultTableModel을 생성하는 메서드
    // DB에서 쿼리를 실행한 결과를 ResultSet에 저장하여, createTableModel메서드에서 결과를 테이블 형태로 표현.
	
    private static DefaultTableModel createTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();//결과집합의 메타데이터를 가져온다.(결과 집합의 열 수, 각 열의 이름 등 정보 저장)
        int columnCount = metaData.getColumnCount();//결과집합의 열 수를 저장하는 columnCount변수

        String[] columnNames = new String[columnCount];//각 열의 이름을 columnNames배열에 저장.
        for (int i = 1; i <= columnCount; i++) {//for문으로 각 열의 이름을 가져와 배열에 저장한다.
            columnNames[i - 1] = metaData.getColumnName(i);
        }
        //DefaultTableModel 객체를 생성하여 열 이름을 갖는 빈 모델을 생성
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        while (resultSet.next()) {//루프를 돌며 ResultSet에서 각 행을 반복적으로 가져와 rowData배열에 저장한다.
            Object[] rowData = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowData[i - 1] = resultSet.getObject(i);
                
            }
            tableModel.addRow(rowData);//테이블 모델을 행에 추가한다.
        }

        return tableModel;//마지막으로, 생성된 디폴트테이블모델 객체를 반환한다.
    }
	
    private static void updateTables(JPanel rightPanel) {
        rightPanel.removeAll();

        String[] tableQueries = {"SELECT Sid,FName, Addr FROM STUDENT", "SELECT SecId, Bldg, RoomNo FROM SECTION",
                "SELECT CoName, Credits, CCode, CDesc FROM COURSE"};

        try (Statement stmt = con.createStatement()) {
            for (int i = (currentPage - 1) * PAGE_SIZE; i < currentPage * PAGE_SIZE && i < tableQueries.length; i++) {
                ResultSet tableResult = stmt.executeQuery(tableQueries[i]);
                DefaultTableModel tableModel = createTableModel(tableResult);
                JTable table = new JTable(tableModel);
                rightPanel.add(new JScrollPane(table));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        rightPanel.revalidate();
        rightPanel.repaint();
    }
  
}
