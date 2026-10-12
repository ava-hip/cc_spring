package fr.dawan.cc_spring.exception;

public class ConsultationNonModifiableException extends RuntimeException {
 public ConsultationNonModifiableException(String numero) {
        super("La consultation " + numero + " est passée et ne peut plus être modifiée");
    }
}
