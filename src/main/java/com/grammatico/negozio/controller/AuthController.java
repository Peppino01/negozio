package com.grammatico.negozio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.grammatico.negozio.DTO.inputDTO.ClienteInputDTO;
import com.grammatico.negozio.DTO.inputDTO.LoginInputDTO;
import com.grammatico.negozio.DTO.inputDTO.mapper.ClienteInputDTOMapper;
import com.grammatico.negozio.DTO.inputDTO.mapper.LoginInputDTOMapper;
import com.grammatico.negozio.DTO.outputDTO.LoginOutputDTO;
import com.grammatico.negozio.DTO.outputDTO.mapper.LoginOutputDTOMapper;
import com.grammatico.negozio.model.Login;
import com.grammatico.negozio.model.Ruolo;
import com.grammatico.negozio.model.entity.Cliente;
import com.grammatico.negozio.service.interfaces.IClienteService;
import com.grammatico.negozio.service.interfaces.IDipendenteService;
import com.grammatico.negozio.service.interfaces.IProprietarioService;

@RestController
public class AuthController {

    private final LoginInputDTOMapper loginInputDTOMapper;
    private final LoginOutputDTOMapper loginOutputDTOMapper;
    private final ClienteInputDTOMapper clienteInputDTOMapper;
    private final IClienteService clienteService;
    private final IDipendenteService dipendenteService;
    private final IProprietarioService proprietarioService;

    public AuthController(
        LoginInputDTOMapper loginInputDTOMapper,
        LoginOutputDTOMapper loginOutputDTOMapper,
        ClienteInputDTOMapper clienteInputDTOMapper,
        IClienteService clienteService,
        IDipendenteService dipendenteService,
        IProprietarioService proprietarioService
    ) {
        this.loginInputDTOMapper = loginInputDTOMapper;
        this.loginOutputDTOMapper = loginOutputDTOMapper;
        this.clienteInputDTOMapper = clienteInputDTOMapper;
        this.clienteService = clienteService;
        this.dipendenteService = dipendenteService;
        this.proprietarioService = proprietarioService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginOutputDTO> login(@RequestBody LoginInputDTO loginInputDTO) {
        // mappo loginInputDTO in un oggetto di tipo Login
        Login login = loginInputDTOMapper.apply(loginInputDTO);

        // controllo se le credenziali sono valide
        if (!login.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // cerco l'utente proprietari, dipendenti e clienti
        try {
            if (clienteService.checkCredentials(login.getEmail(), login.getPassword())) {
                return ResponseEntity.ok(loginOutputDTOMapper.apply(Ruolo.CLIENTE));
            }
            else if (dipendenteService.checkCredentials(login.getEmail(), login.getPassword())) {
                return ResponseEntity.ok(loginOutputDTOMapper.apply(Ruolo.DIPENDENTE));
            }
            else if (proprietarioService.checkCredentials(login.getEmail(), login.getPassword())) {
                return ResponseEntity.ok(loginOutputDTOMapper.apply(Ruolo.PROPRIETARIO));
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody ClienteInputDTO clienteInputDTO) {
        Cliente cliente = clienteInputDTOMapper.apply(clienteInputDTO);

        if (!cliente.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("I dati inseriti per la registrazione non sono validi");
        }

        if (clienteService.checkExistsByEmail(cliente.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Questa mail è già registrata");
        }

        try {
            if(!clienteService.insertCliente(cliente).isValid()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la creazione dell'utente");
            }
        } catch (Exception e) {
            System.out.println("Errore sconosciuto\n" + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore sconosciuto");
        }
        
        return ResponseEntity.ok().build();
    }

}
