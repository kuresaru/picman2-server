package top.scraft.picmanserver.data;

public class PictureLibraryFullException extends Exception {

    public PictureLibraryFullException() {
        super("图库容量已满");
    }

}
