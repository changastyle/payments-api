package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BaseEntity
{
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltMod;
    boolean activo = true;
}
