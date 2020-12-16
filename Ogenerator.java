import java.io.*;
import java.lang.reflect.Array;
import java.nio.*;
import java.util.ArrayList;

public class Ogenerator {
    String outputFile;

    public Ogenerator(String outputFile) {
        this.outputFile = outputFile;
    }

    //指令的十六进制 第一操作数长度 第二操作数长度
    Pair<Integer,Integer > getInstruction(String opCode){
        switch (opCode){
            //int--1 u32--2 u64--3
            //0x00	nop	-	-	-
            case "nop": return new Pair<>(0x00,0);
            //0x01	push	num:u64	-	1:num
            case "push": return new Pair<>(0x01,3);
            //0x02	pop	-	1
            case "pop": return new Pair<>(0x02,0);
            //0x03	popn	num:u32	1-num	-
            case "popn": return new Pair<>(0x03,2);
            //0x04	dup	-	1:num	1:num, 2:num
            case "dup": return new Pair<>(0x04,0);
            //0x0a	loca	off:u32	-	1:addr
            case "loca": return new Pair<>(0x0a,2);
            //0x0b	arga	off:u32	-	1:addr
            case "arga": return new Pair<>(0x0b,2);
            //0x0c	globa	n:u32	-	1:addr
            case "globa": return new Pair<>(0x0c,2);
            //0x10	load.8	-	1:addr	1:val
            case "load.8": return new Pair<>(0x10,0);
            //0x11	load.16	-	1:addr	1:val
            case "load.16": return new Pair<>(0x11,0);
            //0x12	load.32	-	1:addr	1:val
            case "load.32": return new Pair<>(0x12,0);
            //0x13	load.64	-	1:addr	1:val
            case "load.64": return new Pair<>(0x13,0);
            //0x14	store.8	-	1:addr, 2:val
            case "store.8": return new Pair<>(0x14,0);
            //0x15	store.16	-	1:addr, 2:val	-
            case "store.16": return new Pair<>(0x15,0);
            //0x16	store.32	-	1:addr, 2:val	-
            case "store.32": return new Pair<>(0x16,0);
            //0x17	store.64	-	1:addr, 2:val	-
            case "store.64": return new Pair<>(0x17,0);
            //0x18	alloc	-	1:size	1:addr
            case "alloc": return new Pair<>(0x18,0);
            //0x19	free	-	1:addr	-
            case "free": return new Pair<>(0x19,0);
            //0x1a	stackalloc	size:u32	-	-
            case "stackalloc": return new Pair<>(0x1a,2);
            //0x20	add.i	-	1:lhs, 2:rhs	1:res
            case "add.i": return new Pair<>(0x20,0);
            //0x21	sub.i	-	1:lhs, 2:rhs	1:res
            case "sub.i": return new Pair<>(0x21,0);
            //0x22	mul.i	-	1:lhs, 2:rhs	1:res
            case "mul.i": return new Pair<>(0x22,0);
            //0x23	div.i	-	1:lhs, 2:rhs	1:res
            case "div.i": return new Pair<>(0x23,0);
            //0x24	add.f	-	1:lhs, 2:rhs	1:res
            case "add.f": return new Pair<>(0x24,0);
            //0x25	sub.f	-	1:lhs, 2:rhs	1:res
            case "sub.f": return new Pair<>(0x25,0);
            //0x26	mul.f	-	1:lhs, 2:rhs	1:res
            case "mul.f": return new Pair<>(0x26,0);
            //0x27	div.f	-	1:lhs, 2:rhs	1:res
            case "div.f": return new Pair<>(0x27,0);
            //0x28	div.u	-	1:lhs, 2:rhs	1:res
            case "div.u": return new Pair<>(0x28,0);
            //0x29	shl	-	1:lhs, 2:rhs	1:res
            case "shl": return new Pair<>(0x29,0);
            //0x2a	shr	-	1:lhs, 2:rhs	1:res
            case "shr": return new Pair<>(0x2a,0);
            //0x2b	and	-	1:lhs, 2:rhs	1:res
            case "and": return new Pair<>(0x2b,0);
            //0x2c	or	-	1:lhs, 2:rhs	1:res
            case "or": return new Pair<>(0x2c,0);
            //0x2d	xor	-	1:lhs, 2:rhs	1:res
            case "xor": return new Pair<>(0x2d,0);
            //0x2e	not	-	1:lhs	1:res
            case "not": return new Pair<>(0x2e,0);
            //0x30	cmp.i	-	1:lhs, 2:rhs	1:res
            case "cmp.i": return new Pair<>(0x30,0);
            //0x31	cmp.u	-	1:lhs, 2:rhs	1:res
            case "cmp.u": return new Pair<>(0x31,0);
            //0x32	cmp.f	-	1:lhs, 2:rhs	1:res
            case "cmp.f": return new Pair<>(0x32,0);
            //0x34	neg.i	-	1:lhs	1:res
            case "neg.i": return new Pair<>(0x34,0);
            //0x35	neg.f	-	1:lhs	1:res
            case "neg.f": return new Pair<>(0x35,0);
            //0x36	itof	-	1:lhs	1:res
            case "itof": return new Pair<>(0x36,0);
            //0x37	ftoi	-	1:lhs	1:res
            case "ftoi": return new Pair<>(0x37,0);
            //0x38	shrl	-	1:lhs, 2:rhs
            case "shrl": return new Pair<>(0x38,0);
            //0x39	set.lt	-	1:lhs	1:res
            case "set.lt": return new Pair<>(0x39,0);
            //0x3a	set.gt	-	1:lhs	1:res
            case "set.gt": return new Pair<>(0x3a,0);
            //0x41	br	off:i32
            case "br": return new Pair<>(0x41,1);
            //0x42	br.false	off:i32	1:test
            case "br.false": return new Pair<>(0x42,1);
            //0x43	br.true	off:i32	1:test
            case "br.true": return new Pair<>(0x43,1);
            //0x48	call	id:u32
            case "call": return new Pair<>(0x48,2);
            //0x49	ret	-
            case "ret": return new Pair<>(0x49,0);
            //0x4a	callname	id:u32
            case "callname": return new Pair<>(0x4a,2);
            //0x50	scan.i	-	-	1:n
            case "scan.i": return new Pair<>(0x50,0);
            //0x51	scan.c	-	-
            case "scan.c": return new Pair<>(0x51,0);
            //0x52	scan.f	-	-	1:f
            case "scan.f": return new Pair<>(0x52,0);
            //0x54	print.i	-	1:x	-
            case "print.i": return new Pair<>(0x54,0);
            //0x55	print.c	-	1:c	-
            case "print.c": return new Pair<>(0x55,0);
            //0x56	print.f	-	1:f	-
            case "print.f": return new Pair<>(0x56,0);
            //0x57	print.s	-	1:i	-
            case "print.s": return new Pair<>(0x57,0);
            //0x58	println	-	-	-
            case "println": return new Pair<>(0x58,0);
            //0xfe	panic
            case "panic": return new Pair<>(0xfe,0);
        }
        return new Pair<>(-1,-1);
    }

    byte[] intToBytes(int input){
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(input);
        return buffer.array();
    }

    byte[] byteToBytes(Byte input){
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(input);
        return buffer.array();
    }

    byte[] longToBytes(long input){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(input);
        return buffer.array();
    }

    byte[] stringToBytes(String input){
        int len = input.length();
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.order(ByteOrder.BIG_ENDIAN);
        for(int i=0;i<len;i++){
            char ch = input.charAt(i);
            int assc = Integer.valueOf(ch);
            byte out = (byte)(assc & 0XF);
            buffer.put(out);
        }
        return buffer.array();
    }


    public void generate(){
        try {
            File f = new File(outputFile);
            if(f.exists() && f.isFile())
                f.delete();
            if(f.isDirectory()) {
                System.out.println("this is a directory, are you sure you typed the correct output file?");
                System.exit(0);
            }
            f.createNewFile();
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile, true));
            writeMagicAndVersion(out);
            writeglobals(out);
            writeStartCode(out);
            writeFunctionCode(out);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void writeMagicAndVersion(DataOutputStream out){
        try{
            int magic = 0x72303b3e;
            int version = 0x00000001;
            out.write(intToBytes(magic));
            out.write(intToBytes(version));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    void writeglobals(DataOutputStream out){
        try{
            ArrayList<Variable> variables = startcode.getStartCodeTable().variables;
            int count = (int)variables.size();
            out.write(intToBytes(count));
            for(int i=0;i<count;i++){
                byte is_const = 0;
                if(variables.get(i).is_const)
                    is_const = 1;
                String value = variables.get(i).getValue();
                int length = (int)value.length();
                out.write(byteToBytes(is_const));
                out.write(intToBytes(length));
                out.write(value.getBytes());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void writeStartCode(DataOutputStream out){
        try{
            ArrayList<Function> funcs = Functionarrary.getFunctionTable().functions;
            int len = funcs.size();
            out.write(intToBytes(len));

            ArrayList<Order> orders = startcode.getStartCodeTable().orders;
            int count = orders.size();

            int name = startcode.getStartCodeTable().get_index("_start");
            int return_slots = 0;
            int param_slots = 0;
            int loc_slots = 0;

            out.write(intToBytes(name));
            out.write(intToBytes(return_slots));
            out.write(intToBytes(param_slots));
            out.write(intToBytes(loc_slots));
            out.write(intToBytes(count));

            for(int i=0;i<count;i++){
                Order order = orders.get(i);
                writeorder(out,order);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void writeFunctionCode(DataOutputStream out){
        try{
            Functionarrary func_arrary = Functionarrary.getFunctionTable();
            int len = func_arrary.functions.size();
            for (int i=1;i<len;i++){
                Function this_func = func_arrary.functions.get(i);
                int name = startcode.getStartCodeTable().get_index(this_func.name);
                int return_slots = 0;
                if(this_func.type.equals("int") || this_func.type.equals("double"))
                    return_slots = 1;
                int param_slots = this_func.parameters.size();
                int loc_slots = this_func.variables.size();
                int count = this_func.orders.size();

                out.write(intToBytes(name));
                out.write(intToBytes(return_slots));
                out.write(intToBytes(param_slots));
                out.write(intToBytes(loc_slots));
                out.write(intToBytes(count));

                int len_func = this_func.orders.size();
                for(int j=0;j<len_func;j++){
                   Order order = this_func.orders.get(j);
                    writeorder(out,order);
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void writeorder(DataOutputStream out, Order order){
        try{
            Pair<Integer,Integer> instruction = getInstruction(order.getOpcode());
            byte opcode = (byte)instruction.getFirst().intValue();
            out.write(opcode);
            int par = instruction.getSecond();
            if(par == 1 || par == 2){
                int op = (int)order.last_oper();
                out.write(intToBytes(op));
            }
            else if(par == 3){
                long op = (long)order.last_oper();
                out.write(longToBytes(op));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
