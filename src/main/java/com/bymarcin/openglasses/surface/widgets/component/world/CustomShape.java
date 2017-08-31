/*
import com.bymarcin.openglasses.surface.widgets.core.attribute.I2DVertex;
import com.bymarcin.openglasses.surface.widgets.core.attribute.I3DVertex;


	public float[][] vertices = new float[33][3];

	public int getVertexCount() {
		return vertices.length;
	}

	public void setVertex(int n, double nx, double ny, double nz) {
		this.vertices[n][0] = (float) nx;
		this.vertices[n][1] = (float) ny;
		this.vertices[n][2] = (float) nz;
	}
	
	public void setVertex(int n, double nx, double ny) {
		this.vertices[n][0] = (float) nx;
		this.vertices[n][1] = (float) ny;
		this.vertices[n][2] = 0;
	}
	
	public void writeDataVERTICES(ByteBuf buff) {
		int cnt = (int) vertices.length;
		buff.writeInt(cnt);
		for(int i=0; i < cnt; i++){
			buff.writeFloat((float) vertices[i][0]);
			buff.writeFloat((float) vertices[i][1]);
			buff.writeFloat((float) vertices[i][2]);
		}
	}
	
	public void readDataVERTICES(ByteBuf buff) {
		int cnt = (int) buff.readInt();
		for(int i=0; i < cnt; i++){
			vertices[i][0] = (float) buff.readFloat();
			vertices[i][1] = (float) buff.readFloat();
			vertices[i][2] = (float) buff.readFloat();			
		}
	}
	
*/
