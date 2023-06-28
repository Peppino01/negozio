package com.grammatico.negozio.DTO.outputDTO;

import java.sql.Date;

public record ProprietarioOutputDTO (
    Long id,
    String nome,
    String cognome,
    String email,
    String password,
    String numTelefono,
    Date dataNascita
) {
    
}
