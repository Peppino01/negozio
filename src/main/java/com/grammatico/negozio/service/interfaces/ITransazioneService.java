package com.grammatico.negozio.service.interfaces;

import java.util.List;

import com.grammatico.negozio.DTO.outputDTO.TransazioneSummaryOutputDTO;

public interface ITransazioneService {

    List<TransazioneSummaryOutputDTO> getSummary();
    
}
