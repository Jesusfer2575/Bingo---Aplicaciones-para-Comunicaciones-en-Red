import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente4Anuncio{
	private static int usuario = 4;
	private static int [][]mi_matriz;
	private static boolean [][]m_llenar = new boolean[7][7];
	private static int soy;
	private static boolean es_Ganador = false;	
	
	public static void enviaNombre(String nom,InetAddress gpo,MulticastSocket cl){
		//Enviamos nuestro nombre como Cliente1
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(baos);
			os.flush();
			os.writeObject(nom);
			byte []b = baos.toByteArray();
			cl.send(new DatagramPacket(b,b.length,gpo,8888));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void imprimeMatriz(){
		//Imprimimos nuestra matriz
			for(int i=0;i<6;i++){
				for(int j=0;j<6;j++){
					System.out.print(mi_matriz[i][j] + " ");
				}System.out.println();
			}
			//Thread.sleep(2000);
	}
	
	public static void recibeMatriz(MulticastSocket cl){
		//Create buffer
		try{
			boolean flag = false;
			for(;;){
			   byte[] buffer = new byte[1024];
			   cl.receive(new DatagramPacket(buffer,1024));
			   System.out.println("Tablero recibido!");

			   //Deserialze object
			   ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			   ObjectInputStream ois = new ObjectInputStream(bais);
			   for(int i=0;i<4;i++){
			   	Matriz mz = (Matriz)ois.readObject();		      	
			   	soy = mz.getUsuario();
			   	
			   	if(soy == usuario){
			   		mi_matriz = mz.getMatrix();
			   		flag = true;
			   		break;
			   	}
			   }
			   System.out.println("Soy el usuario "+soy+ "!!!\n");
			   if(flag)break;
			}
			imprimeMatriz();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void tachaNumeroMatriz(int n){
		for(int i=0;i<6;i++)
			for(int j=0;j<6;j++)
				if(n == mi_matriz[i][j]){
					m_llenar[i][j] = true;
					break;
				}	
	}
	
	public static void setMatrizToFalse(){
		for(int i=0;i<6;i++)
			for(int j=0;j<6;j++)
					m_llenar[i][j] = false;
	}
	
	public static boolean verificaMatriz(){
		boolean flag;
		int cont = 0;
		for(int i=0;i<6;i++)
			for(int j=0;j<6;j++)
				if(m_llenar[i][j])
					cont++;
		return (cont==36) ? true:false;			
	}
	
	public static void imprimeChulo(){
		for(int i=0;i<6;i++){
			for(int j=0;j<6;j++){
				if(m_llenar[i][j]) 
					System.out.print("x  ");
				else	
					System.out.print(mi_matriz[i][j] + " ");		
			}System.out.println("");
		}
		System.out.println("-------------------");
	}
	
	public static void recibeNumero(MulticastSocket cl,InetAddress gpo){
		int estado = 0;
		try{
			for(;;){
				byte[] buffer = new byte[1024];
				cl.receive(new DatagramPacket(buffer,1024));

				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
				ObjectInputStream ois = new ObjectInputStream(bais);
				Integer numerito = (Integer)ois.readObject();

				tachaNumeroMatriz(numerito);
				imprimeChulo();
				// Caso en el que ya gano este jugador!
				if(verificaMatriz()){
					estado = 1;
					es_Ganador = true;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream os = new ObjectOutputStream(baos);
					os.writeObject(new Integer(estado));
					os.flush();
					os.close();
					byte []b = baos.toByteArray();
					cl.send(new DatagramPacket(b,b.length,gpo,8888));
					break;
				}else{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream os = new ObjectOutputStream(baos);
					os.writeObject(new Integer(estado));
					os.flush();
					os.close();
					byte []b = baos.toByteArray();
					cl.send(new DatagramPacket(b,b.length,gpo,8888));
				}
		   }
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void main(String args[]){
		String nombre = args[0];
		setMatrizToFalse();
		try{
			ArrayList<Matriz> recibida = new ArrayList<Matriz>();
			MulticastSocket cl = new MulticastSocket(8887);
			InetAddress grupo = InetAddress.getByName("229.1.2.3");
			cl.joinGroup(grupo);
			System.out.println("Cliente unido al grupo");
			cl.setReuseAddress(true);
			
			//Enviamos nuestro nombre al servidor
			enviaNombre(nombre,grupo,cl);
			
			//Recibimos la matriz
			recibeMatriz(cl);
			
			recibeNumero(cl,grupo);
			
			if(es_Ganador)
				System.out.println("Soy el GANADOR!!!");

		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
}
