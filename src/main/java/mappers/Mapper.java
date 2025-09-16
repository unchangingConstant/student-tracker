package mappers;

public interface Mapper<S, T> {

    public T map(S src);

}
