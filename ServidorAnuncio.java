import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorAnuncio{
	private static int [][]m_cliente1 = new int[7][7];
	private static int [][]m_cliente2 = new int[7][7];
	private static int [][]m_cliente3 = new int[7][7];
	private static int [][]m_cliente4 = new int[7][7];
	private static boolean []cubeta = new boolean[151];
	
	private static ArrayList<Matriz> matrices;
	private static Set<String> setNombres;
	
	public static void generaMatrices(){
		Random rnd = new Random();
		for(int i=0;i<6;i++){
			for(int j=0;j<6;j++){
				int k;
				while(true){
					k = rnd.nextInt(149);
					if(!cubeta[k]){
						cubeta[k] = true;
						break;
					}	
				}
				m_cliente1[i][j] = k;
			}
		}
		for(int i=0;i<6;i++){
			for(int j=0;j<6;j++){
				int k;
				while(true){
					k = rnd.nextInt(149);
					if(!cubeta[k]){
						cubeta[k] = true;
						break;
					}	
				}
				m_cliente2[i][j] = k;
			}
		}
		for(int i=0;i<6;i++){
			for(int j=0;j<6;j++){
				int k;
				while(true){
					k = rnd.nextInt(149);
					if(!cubeta[k]){
						cubeta[k] = true;
						break;
					}	
				}
				m_cliente3[i][j] = k;
			}
		}
		for(int i=0;i<6;i++){
			for(int j=0;j<6;j++){
				int k;
				while(true){
					k = rnd.nextInt(149);
					if(!cubeta[k]){
						cubeta[k] = true;
						break;
					}	
				}
				m_cliente4[i][j] = k;
			}
		}
		/*for(int i=0;i<6;i++){
			for(int j=0;j<6;j++){
				System.out.print(m_cliente4[i][j] + " ");	
			}System.out.print("\n");
		}*/
	}
	
	public static ArrayList llenaArray(){
		generaMatrices();
		Matriz m1 = new Matriz(1,m_cliente1);
		Matriz m2 = new Matriz(2,m_cliente2);
		Matriz m3 = new Matriz(3,m_cliente3);
		Matriz m4 = new Matriz(4,m_cliente4);
		ArrayList<Matriz> matrices = new ArrayList<Matriz>();
		matrices.add(m1);
		matrices.add(m2);
		matrices.add(m3);
		matrices.add(m4);
		return matrices;
	}
	
	public static void recibeNombres(MulticastSocket s){
		try{
			setNombres = new HashSet<String>();
			for(;;){
				byte[] buffer = new byte[1024];
				s.receive(new DatagramPacket(buffer,1024));

				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
				ObjectInputStream ois = new ObjectInputStream(bais);
				String nombresito = (String)ois.readObject();
				setNombres.add(nombresito);
				System.out.println(nombresito+" logged!");
				System.out.println("Nombre(s) "+setNombres.size() + " recibido");
				if(setNombres.size() == 4)
					break;
		   }
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void enviaMatriz(MulticastSocket s,InetAddress gpo){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(baos);
			os.flush();
			for(int i=0;i<matrices.size();i++)
				os.writeObject(matrices.get(i));
			byte []b = baos.toByteArray();
			DatagramPacket p = new DatagramPacket(b,b.length,gpo,8887);
			s.send(p);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void enviaNumeros(MulticastSocket s,InetAddress gpo){
		boolean []random_diferentes = new boolean[151];
		
		for(int i=0;i<145;i++){
			try{
				Random rnd = new Random();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(baos);
				int k;
				while(true){
					k = rnd.nextInt(149);
					if(!random_diferentes[k]){
						random_diferentes[k] = true;
						break;
					}
				}
				System.out.print(k + " ");
				os.writeObject(new Integer(k));
				
				os.flush();
				os.close();

				byte []b = baos.toByteArray();
				DatagramPacket p = new DatagramPacket(b,b.length,gpo,8887);
				s.send(p);
				Thread.sleep(500);
				
				byte[] buffer = new byte[1024];
				s.receive(new DatagramPacket(buffer,1024));
				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
				ObjectInputStream is = new ObjectInputStream(bais);
				int estado = (Integer)is.readObject();
				//System.out.println("Estado: " + estado);
				if(estado==1)
					break;

			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	public static void main(String []args){
		matrices = llenaArray();

		try{
			MulticastSocket s = new MulticastSocket(8888);
			InetAddress grupo = null;
			try{
				grupo = InetAddress.getByName("229.1.2.3");
			}catch(UnknownHostException uhe){
				System.out.print("Direccion no valida");
				System.exit(0);
			}
			s.joinGroup(grupo);
			System.out.println("Unido al grupo 229.1.2.3\nEsperando nombres...");
			
			recibeNombres(s);
			
			enviaMatriz(s,grupo);
			
			enviaNumeros(s,grupo);
						
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

}
