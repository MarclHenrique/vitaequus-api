ALTER TABLE tb13Parto
    ADD COLUMN observacoes TEXT NULL;

ALTER TABLE tb14PotroNascido
    ADD COLUMN pelagem_inicial VARCHAR(80) NULL,
    ADD COLUMN condicao_neonato VARCHAR(20) NULL,
    ADD COLUMN fktb04idAnimal BIGINT NULL;

ALTER TABLE tb14PotroNascido
    ADD INDEX idx_potro_nascido_animal (fktb04idAnimal);

ALTER TABLE tb14PotroNascido
    ADD CONSTRAINT fk_potro_nascido_animal
        FOREIGN KEY (fktb04idAnimal)
        REFERENCES tb04Animal (idAnimal);
