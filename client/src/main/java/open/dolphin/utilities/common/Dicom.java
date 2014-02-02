/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.common;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Formatter;

/**
 * DICOMクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class Dicom extends AbstractCommonFunc {
    
    protected boolean timeCheck = true;
    protected long start;
    protected long stop;
    protected int setVM = 0;

    protected boolean Implisit = true;
    protected boolean meta = false;
    protected int dep;
    protected int mono;
    protected int minus;
    protected float RescaleS;
    protected float RescaleI;
    protected byte[] out8Data;    //Raw
    protected short[] out16Data;  //Raw->SwapRaw
    protected byte outdata[];     //RGB(24bit)
    protected DataOutputStream dis;
    protected ByteArrayOutputStream bmpData;
    protected byte[] rawData;
    protected int outDataSize;
    protected int dataWidth;
    protected int dataHeight;
    protected float wcVal;
    protected float wwVal;
    protected float wcOffset;
    protected float wwOffset;
    
    /**
     * コンストラクタ
     */
    protected Dicom(){
        super();
        Init();
    }
    
    /**
     * 初期化
     */
    void Init() {
        dep = 0;
        mono = 0;
        minus = 0;
        RescaleI = 0;
        RescaleS = 1;
        out8Data = null;
        out16Data = null;
        outdata = null;
        //UL = setString2VR("UL");
        dis = null;
        bmpData = null;
        rawData = null;
        outDataSize = 0;
        dataWidth = 0;
        dataHeight = 0;
        wwVal = 0f;
        wcVal = 0f;
        wwOffset = 0f;
        wcOffset = 0f;
    }
    
    /**
     * DICOMファイルのオープン
     * @param dcmFname ファイルパス
     * @return 結果
     */
    protected int dcm2bmpOpen(String dcmFname){
        int ret =0;
        ret = dcmGetraw(dcmFname);
        if(mono != 2){
            ret = dcmRaw2bmp();
            ret = dcmRawFix();
        }

        return(ret);
    }
    
    /**
     * 階調処理
     * @param wc センター
     * @param ww 幅
     * @return 結果
     */
    protected int changeWCWW(float wc, float ww){
        int ret =0;
        wwOffset += ww;
        wcOffset += wc;
        if(mono != 2){
            //ret = dcmRaw2bmp();
            ret = dcmRawFix();
        }

        return(ret);
    }
    
    /**
     * 階調クリア
     * @return 結果
     */
    protected int clearWCWW(){
        int ret =0;
        wwOffset = 0;
        wcOffset = 0;
        if(mono != 2){
            //ret = dcmRaw2bmp();
            ret = dcmRawFix();
        }

        return(ret);
    }
    
    /**
     * BMPファイルの保存
     * @param bmpFname ファイルパス
     * @param kind 種類
     * @return 結果
     */
    protected int dcm2bmpWrite(String bmpFname, int kind){
        int ret =0;
        if(mono == 2){
            ret = dcmBmpRGBWrite(bmpFname, kind);

        } else {
            ret = dcmBmpGrayWrite(bmpFname, kind);
        }

        
        return(ret);
    }
    
    /**
     * RGBの保存
     * @param bmpFname ファイルパス
     * @param kind 種類
     * @return 結果
     */
    protected int dcmBmpRGBWrite(String bmpFname, int kind){
        if(timeCheck){
            System.out.println("bmp Start");
            start = System.currentTimeMillis();
        }
        int ret =0;
        int i, j;
	//int	bit=1;
	int real_width;
        //byte header_buf[];
        int file_size;
        //int offset_to_data;
        //long info_header_size;
        short planes;
        short color;
        int compress;
        int data_size;
        int xppm;
	int yppm;
        byte data;
        byte[] bmp;
        int bmpsize;
        
        real_width = dataWidth*3 + dataWidth%4;
        file_size = dataHeight * real_width + HEADERSIZE;

        bmp = new byte[file_size+1024];
        
	planes = 1;
	color = 24;
	compress = 0;
	data_size = dataHeight * real_width;
	xppm = 1;
	yppm = 1;
        try{
            //DataOutputStream dis = new DataOutputStream(new FileOutputStream(bmpFname));
            if(kind == 0 && bmpFname != null) {
                dis = new DataOutputStream(new FileOutputStream(bmpFname));
            } else {
                bmpData = new ByteArrayOutputStream();
                dis = new DataOutputStream(bmpData);
            }
            dis.writeByte('B');
            dis.writeByte('M');
            file_size = data2LEUL(file_size);
            dis.writeInt(file_size);
            dis.writeInt(0);//Dummy
            data = HEADERSIZE;
            dis.writeByte(data);
            dis.writeByte(0);
            dis.writeByte(0);
            dis.writeByte(0);
            data = INFOHEADERSIZE;
            dis.writeByte(data);
            dis.writeByte(0);
            dis.writeByte(0);
            dis.writeByte(0);
            int swidth = data2LEUL(dataWidth);
            int sheight = data2LEUL(dataHeight);
            short splanes = data2LEUS(planes);
            short scolor = data2LEUS(color);
            dis.writeInt(swidth);
            dis.writeInt(sheight);
            dis.writeShort(splanes);
            dis.writeShort(scolor);
            dis.writeInt(compress);
            int sdata_size = data2LEUL(data_size);
            dis.writeInt(sdata_size);
            int sxppm = data2LEUL(xppm);
            int syppm = data2LEUL(yppm);
            dis.writeInt(sxppm);
            dis.writeInt(syppm);
            dis.writeInt(0);//Dummy
            dis.writeInt(0);//Dummy
            //for(i=0; i<height; i++){
            if(false){
                for(i=dataHeight-1; i>=0; i--){
                    bmp = new byte[real_width];
                    for(j=0; j<dataWidth; j++){
			bmp[(j*3)] = out8Data[(i*dataWidth)+j];
                        bmp[(j*3)+1] = out8Data[(i*dataWidth)+j];
                        bmp[(j*3)+2] = out8Data[(i*dataWidth)+j];
                    }
                    //RGB情報を4バイトの倍数に合わせている
                    for(j=dataWidth*3; j<real_width; j++){
			bmp[j] = 0;
                    }
                    dis.write(bmp,0,real_width);
                    bmpsize = real_width;
                }
            }else{
                bmp = new byte[data_size];
                int k,l=0;
                for(i=dataHeight-1; i>=0; i--){
                    k = l*real_width;
                    for(j=0; j<dataWidth; j++){
			//bmp[k+(j*3)] = out8Data[(i*dataWidth*3)+(j*3)+2];
                        //bmp[k+(j*3)+1] = out8Data[(i*dataWidth*3)+(j*3)+1];
                        //bmp[k+(j*3)+2] = out8Data[(i*dataWidth*3)+(j*3)];
			byte b = out8Data[(i*dataWidth*3)+(j*3)+2];
                        byte g = out8Data[(i*dataWidth*3)+(j*3)+1];
                        byte r = out8Data[(i*dataWidth*3)+(j*3)];
			short bTmp = (short) (b < 0 ? b + 256 : b);
                        short gTmp = (short) (g < 0 ? g + 256 : g);
                        short rTmp = (short) (r < 0 ? r + 256 : r);
                        bTmp = (short) (bTmp+wcOffset > 255 ? 255 : bTmp+wcOffset < 0 ? 0 : bTmp+wcOffset);
                        gTmp = (short) (gTmp+wcOffset > 255 ? 255 : gTmp+wcOffset < 0 ? 0 : gTmp+wcOffset);
                        rTmp = (short) (rTmp+wcOffset > 255 ? 255 : rTmp+wcOffset < 0 ? 0 : rTmp+wcOffset);
			bmp[k+(j*3)] = (byte) bTmp;
                        bmp[k+(j*3)+1] = (byte) gTmp;
                        bmp[k+(j*3)+2] = (byte) rTmp;
                    }
                    //RGB情報を4バイトの倍数に合わせている
                    for(j=dataWidth*3; j<real_width; j++){
			bmp[k+j] = 0;
                    }
                    l++;
                }
                dis.write(bmp,0,data_size);
                bmpsize = data_size;
            }
            dis.close();

        }
        catch(Exception e){
            System.out.println("書き込みに失敗");
            ret = -1;

        }
        if(timeCheck){
            stop = System.currentTimeMillis();
            System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
            System.out.println("bmp End");
        }
        return(ret);
    }
    
    /**
     * グレースケールの保存
     * @param bmpFname ファイルパス
     * @param kind 種類
     * @return 結果
     */
    protected int dcmBmpGrayWrite(String bmpFname, int kind){
        if(timeCheck){
            System.out.println("bmp Start");
            start = System.currentTimeMillis();
        }
        int ret =0;
        int i, j;
	//int	bit=1;
	int real_width;
        //byte header_buf[];
        int file_size;
        //int offset_to_data;
        //long info_header_size;
        short planes;
        short color;
        int compress;
        int data_size;
        int xppm;
	int yppm;
        byte data;
        byte[] bmp;
        int bmpsize;

        real_width = dataWidth*3 + dataWidth%4;
        file_size = dataHeight * real_width + HEADERSIZE;

        bmp = new byte[file_size+1024];

	planes = 1;
	color = 24;
	compress = 0;
	data_size = dataHeight * real_width;
	xppm = 1;
	yppm = 1;
        try{
            //DataOutputStream dis = new DataOutputStream(new FileOutputStream(bmpFname));
            if(kind == 0 && bmpFname != null) {
                dis = new DataOutputStream(new FileOutputStream(bmpFname));
            } else {
                bmpData = new ByteArrayOutputStream();
                dis = new DataOutputStream(bmpData);
            }
            dis.writeByte('B');
            dis.writeByte('M');
            file_size = data2LEUL(file_size);
            dis.writeInt(file_size);
            dis.writeInt(0);//Dummy
            data = HEADERSIZE;
            dis.writeByte(data);
            dis.writeByte(0);
            dis.writeByte(0);
            dis.writeByte(0);
            data = INFOHEADERSIZE;
            dis.writeByte(data);
            dis.writeByte(0);
            dis.writeByte(0);
            dis.writeByte(0);
            int swidth = data2LEUL(dataWidth);
            int sheight = data2LEUL(dataHeight);
            short splanes = data2LEUS(planes);
            short scolor = data2LEUS(color);
            dis.writeInt(swidth);
            dis.writeInt(sheight);
            dis.writeShort(splanes);
            dis.writeShort(scolor);
            dis.writeInt(compress);
            int sdata_size = data2LEUL(data_size);
            dis.writeInt(sdata_size);
            int sxppm = data2LEUL(xppm);
            int syppm = data2LEUL(yppm);
            dis.writeInt(sxppm);
            dis.writeInt(syppm);
            dis.writeInt(0);//Dummy
            dis.writeInt(0);//Dummy
            //for(i=0; i<height; i++){
            if(false){
                for(i=dataHeight-1; i>=0; i--){
                    bmp = new byte[real_width];
                    for(j=0; j<dataWidth; j++){
			bmp[(j*3)] = outdata[(i*dataWidth)+j];
                        bmp[(j*3)+1] = outdata[(i*dataWidth)+j];
                        bmp[(j*3)+2] = outdata[(i*dataWidth)+j];
                    }
                    //RGB情報を4バイトの倍数に合わせている
                    for(j=dataWidth*3; j<real_width; j++){
			bmp[j] = 0;
                    }
                    dis.write(bmp,0,real_width);
                    bmpsize = real_width;
                }
            }else{
                bmp = new byte[data_size];
                int k,l=0;
                for(i=dataHeight-1; i>=0; i--){
                    k = l*real_width;
                    for(j=0; j<dataWidth; j++){
			bmp[k+(j*3)] = outdata[(i*dataWidth)+j];
                        bmp[k+(j*3)+1] = outdata[(i*dataWidth)+j];
                        bmp[k+(j*3)+2] = outdata[(i*dataWidth)+j];
                    }
                    //RGB情報を4バイトの倍数に合わせている
                    for(j=dataWidth*3; j<real_width; j++){
			bmp[k+j] = 0;
                    }
                    l++;
                }
                dis.write(bmp,0,data_size);
                bmpsize = data_size;
            }
            dis.close();

        }
        catch(Exception e){
            System.out.println("書き込みに失敗");
            ret = -1;

        }
        if(timeCheck){
            stop = System.currentTimeMillis();
            System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
            System.out.println("bmp End");
        }
        return(ret);
    }
    
    /**
     * 
     * @return 結果
     */
    protected int dcmRawFix(){
        if(timeCheck){
            System.out.println("dcmRawFix Start");
            start = System.currentTimeMillis();
        }
        int ret =0;
        int i,j;   
	double dss;
 
	int ddep =8;
	//int wwdep;
        // dcmGetraw()で処理
        //int wwdep = 1<<dep;
        //if(ww == 0.0 && wc == 0.0){
        //    wc = wwdep/2;
        //    ww = wwdep-1;
        //}
        double wdep = 1<<ddep;
        wdep -= 1;
        outdata = new byte[dataWidth*dataHeight+1024];
        outDataSize = 0;
        float wc = wcVal + wcOffset;
        float ww = wwVal + wwOffset;
        for(i=0; i<dataHeight; i++){          
            for(j=0; j<dataWidth; j++){
                dss = out16Data[(i*dataWidth)+j];
                
                dss = (dss*RescaleS)+RescaleI;
		dss = dss -(wc-(ww/2));
		dss = ((dss*(wdep))/ww);

                if(dss > wdep){
                    dss = wdep;
		}
		if(dss < 0){
			dss = 0;
		}
                if(mono == 1){
                    dss = wdep-dss;
                }
                outdata[(i*dataWidth)+j] = (byte)dss;
                outDataSize += 1;
            }
        }
        if(false){
            try{
                RandomAccessFile randomfile1 = new RandomAccessFile("d:\\img\\aaa.raw", "rw");
                randomfile1.write(outdata);
                randomfile1.close();

            }
            catch(IOException e){
                System.out.println("Error");
                ret = -1;
            }
        }
        if(timeCheck){
            stop = System.currentTimeMillis();
            System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
            System.out.println("dcmRawFix End");
        }
        return(ret);
    }
    
    /**
     * 
     * @return 結果
     */
    protected int dcmRaw2bmp(){
        int k,i,j,ret =0;
        short ss;
        int wi,index;
        if(timeCheck){
            System.out.println("dcmRaw2bmp Start");
            start = System.currentTimeMillis();
        }
        out16Data = new short[(int)out8Data.length];
        wi=0;
        byte ub;
        for(i = 0;i<dataHeight;i++){
            for(j = 0;j<dataWidth*2;j+=2){
              index = (i*dataWidth*2)+j;
              ub = (out8Data[index]);
              ss = (short)(ub & 0xFF);
 
              out16Data[wi] = ss;
              ub = (out8Data[index+1]);
              ss = (short)(ub & 0xFF);

              out16Data[wi] += ss*=0x100;
              //Formatter f = new Formatter();
              //f.format("%04x",out16Data[wc]);
              //String s = f.toString();
              //System.out.println(s);;
              wi++;
             }
         }
        if(timeCheck){
            stop = System.currentTimeMillis();
            System.out.println(wi);
            System.out.println(out16Data.length);
            System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
            System.out.println("dcmRaw2bmp End");
        }
        return(ret);
    }
    
    /**
     * 
     * @param dcmFname ファイルパス
     * @return 結果
     */
    protected int dcmGetraw(String dcmFname){
        //byte[] outData = null;
        //BufferedReader br;
        int ret = 0;
        short g,e;
        int len;
        int index =0;
        byte[] buf;
        byte VR[];
        short svr;
        String s;
        long seek =0;
        int grpelm = 0x00000000;
        try {
            //Read
            //File dcmFile = new File(dcmFname);
            //outData = new char[(int) dcmFile.length()+1024];
            RandomAccessFile dis = new RandomAccessFile(dcmFname,"r");
            //DataInputStream dis = new DataInputStream(new FileInputStream(dcmFname));
            //ByteBuffer bb = ByteBuffer.allocateDirect((int)dcmFile.length()+1024);
            //bb.order(ByteOrder.LITTLE_ENDIAN);
            dis.skipBytes(128);
            buf = new byte[(int)4];
            dis.read(buf);
            VR = new byte[(int)2];
            //seek = dis.getFilePointer();
            s = new String( buf );
            if(s.equals("DICM")){
                meta = true;
                while(true){
                    g = data2LEUS(dis.readShort());
                    e = data2LEUS(dis.readShort());
                    grpelm = geString2Hex(g,e);
                    if(grpelm >= 0x00080000){
                        seek = dis.getFilePointer();               
                        break;
                    }
                    dis.read(VR);
                    svr =geString2VR(VR);
                    switch(svr){
                        case OB:
                        case OW:
                        case SQ:
                        case UN:
                            dis.readShort();//Dummy
                            len = data2LEUL(dis.readInt());
                            break;
                        default:
                            len = (int)data2LEUS(dis.readShort());
                            break;

                    }
                    buf = new byte[(int)len];
                    dis.read(buf);
                     switch(grpelm){
                        case 0x00020010:
                            //null Term
                            if(buf[len-1] == 0x00){
                                buf[len-1] = 0x20;
                            }
                            s = new String( buf );
                            s = s.replaceAll(" ", "");
                            if(s.equals("1.2.840.10008.1.2.1")){
                                Implisit = false;
                            }
                            break;
                    }
                 
                }

            } else {
                dis.seek(0);

            }
            if(meta == true){
                dis.seek(seek-4);
            }
            while(grpelm != 0x7fe00010){
                g = data2LEUS(dis.readShort());
                e = data2LEUS(dis.readShort());
                grpelm = geString2Hex(g,e);

                Formatter f = new Formatter();
                f.format("%08x",grpelm);
                String gps = f.toString();
                System.out.println(gps);

                if(Implisit == false){
                    dis.read(VR);
                    svr =geString2VR(VR);
                     switch(svr){
                        case OB:
                        case OW:
                        case SQ:
                        case UN:
                            dis.readShort();//Dummy
                            len = data2LEUL(dis.readInt());
                            break;
                         default:
                            len = (int)data2LEUS(dis.readShort());
                            break;
                    }
                } else {
                    len = data2LEUL(dis.readInt());
                }                

                switch(grpelm){
                    case 0x00280004:
                        buf = new byte[(int)len];
                        dis.read(buf);
                        s = new String( buf );
                        //MONOCHEROME or RGB
                        if(s.equals("MONOCHROME1 ")){
                            mono = 1;
                        } else if(s.equals("MONOCHROME2 ")){
                            mono = 0;
                        } else if(s.equals("RGB ")){
                            mono = 2;
                        }                      
                        break;
                    case 0x00280010:
                        dataHeight = data2LEUS(dis.readShort());
                        break;
                    case 0x00280011:
                        dataWidth = data2LEUS(dis.readShort());
                        break;
                    case 0x00280101:
                        dep = data2LEUS(dis.readShort());
                        break;
                    case 0x00281050:
                        buf = new byte[(int)len];
                        dis.read(buf);
                        s = new String( buf );
                        wcVal = data2LEFL(s);
                        break;
                    case 0x00281051:
                        buf = new byte[(int)len];
                        dis.read(buf);
                        s = new String( buf );
                        wwVal = data2LEFL(s);
                        break;
                    case 0x00281052:
                        buf = new byte[(int)len];
                        dis.read(buf);
                        s = new String( buf );
                        RescaleI = data2LEFL(s);
                        break;
                    case 0x00281053:
                        buf = new byte[(int)len];
                        dis.read(buf);
                        s = new String( buf );
                        RescaleS = data2LEFL(s);
                        break;
                    case 0x7fe00010:
                        if(timeCheck){
                            start = System.currentTimeMillis();
                        }
                        out8Data = new byte[(int)len];
                        dis.read(out8Data);
                        outDataSize = len;

                        if(timeCheck){
                            stop = System.currentTimeMillis();
                            System.out.println("画像読込みにかかった時間は " + (stop - start) + " ミリ秒です。");
                        }
                        break;
                    default:
                        if(len == 0xFFFFFFFF){
                            ret = readFFFFLength(dis);
  
                        } else {
                            buf = new byte[(int)len];
                            dis.read(buf);
                        }
                        break;
                }
            }        
            int wwdep = 1<<dep;
            if(wwVal == 0.0 && wcVal == 0.0){
                wwVal = wwdep-1;
                wcVal = wwdep/2;
            }
        }
        catch(Exception ex){
            System.out.println("ファイル例外"+ex+"が発生しました");
            ret = -1;
        }
        return(ret);
    }

    /**
     * 
     * @param dis
     * @return 結果
     */
    protected int readFFFFLength(RandomAccessFile dis){
        int ret=0;
        short g,e;
        int grpelm;
        int len;
        byte [] buf;
        boolean loop = true;
        try{
            //System.out.println("readFFFFLengthS "+dis.length()+" が発生しました");
            while(loop){
                g = data2LEUS(dis.readShort());
                e = data2LEUS(dis.readShort());
                grpelm = geString2Hex(g,e);
                switch(grpelm){
                    case 0xFFFEE000:
                            len = data2LEUL(dis.readInt());
                            if(len == 0xFFFFFFFF){
                                ret = readFFFFLength(dis);
                            } else {
                                buf = new byte[(int)len];
                                dis.read(buf);
                            }
                            break;
                    case 0xFFFEE00D:
                           len = data2LEUL(dis.readInt());
                           loop = false;
                           break;
                    case 0xFFFEE0DD:
                           len = data2LEUL(dis.readInt());
                           loop = false;
                           break;
                    default:
                            len = data2LEUL(dis.readInt());
                            buf = new byte[(int)len];
                            dis.read(buf);
                            break;
                }

            }
            //System.out.println("readFFFFLengthE"+dis.length()+"が発生しました");
        }
        catch(Exception ex){
            System.out.println("ファイル例外"+ex+"が発生しました");
            ret = -1;
        }

        return(ret);
    }
    
    /**
     * 
     * @param g グループ
     * @param e エレメント
     * @return 結果
     */
    protected int geString2Hex(int g,int e){
        int GrpElm=0;
        GrpElm = (g<<16) & 0xFFFF0000;
        GrpElm += e & 0x0000FFFF;
        return(GrpElm);
    }
    
    /**
     * 
     * @param VR VR
     * @return VR
     */
    protected short setString2VR(String VR){
        short vr=0;
        int ch;
        ch=  Character.codePointAt(VR, 0);
        vr = (short)(ch*0x100);
        ch=  Character.codePointAt(VR, 1);
        vr += (short)ch;

        return(vr);
    }
    
    /**
     * 
     * @param VR VR
     * @return VR
     */
    protected short geString2VR(byte VR[]){
        short vr=0;
        vr = VR[1];
        vr += VR[0]*0x100;
        return(vr);
    }
    
    /**
     * 
     * @param us US
     * @return US
     */
    protected short data2LEUS(short us){
        short  retUS;
        ByteBuffer bb = ByteBuffer.allocateDirect(2);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putShort(us);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        retUS = bb.getShort(0);
        return(retUS);
    }
    
    /**
     * 
     * @param ul UL
     * @return UL
     */
    protected int data2LEUL(int ul){
        int  retUL;
        ByteBuffer bb = ByteBuffer.allocateDirect(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(ul);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        retUL = bb.getInt(0);
        return(retUL);
    }
    
    /**
     * 
     * @param ds DS
     * @return DS
     */
    protected int data2LEDS(String ds){
        int ids=0;
        ds = ds.replaceAll(" ", "");
        ids = Integer.parseInt(ds);
        return(ids);
    }
    
    /**
     * 
     * @param fl FL
     * @return FL
     */
    protected float data2LEFL(String fl){
        float ifl=0;
        int p;
        p = fl.indexOf('\\');
        if(p == -1){
            fl = fl.replaceAll(" ", "");
        } else {
            fl = fl.substring(0,p);
        }
        ifl = Float.parseFloat(fl);
        return(ifl);
    }
}
