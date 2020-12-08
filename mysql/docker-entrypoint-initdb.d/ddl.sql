CREATE TABLE IF NOT EXISTS `janken`.`players` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE IF NOT EXISTS `janken`.`jankens` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `playedAt` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `janken`.`janken_details` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `janken_id` INT UNSIGNED NOT NULL,
  `player_id` INT UNSIGNED NOT NULL,
  `hand` INT UNSIGNED NOT NULL,
  `result` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `player_id` (`player_id` ASC),
  INDEX `janken_id` (`janken_id` ASC),
  CONSTRAINT `fk_janken_id`
    FOREIGN KEY (`janken_id`)
    REFERENCES `janken`.`jankens` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_player_id`
    FOREIGN KEY (`player_id`)
    REFERENCES `janken`.`players` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

INSERT INTO `janken`.`players` (`id`, `name`) VALUES
(1, 'Alice'),
(2, 'Bob');
