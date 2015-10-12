import java.io.Serializable;

public class Matriz implements Serializable{

	private int usuario;
	private int [][]matriz = new int[6][6];
	
	public Matriz(int usuario,int [][]matriz){
		this.usuario = usuario;
		for(int i=0;i<6;i++)
			for(int j=0;j<6;j++)
				this.matriz[i][j] = matriz[i][j];
	}
	public int[][] getMatrix(){
		return this.matriz;
	}
	public int getUsuario(){
		return this.usuario;
	}

}
