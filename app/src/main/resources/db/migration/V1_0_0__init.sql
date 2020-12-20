CREATE TABLE IF NOT EXISTS `players` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE IF NOT EXISTS `jankens` (
  `id` CHAR(36) NOT NULL,
  `played_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `played_at` (`played_at` ASC)
);

CREATE TABLE IF NOT EXISTS `janken_details` (
  `id` CHAR(36) NOT NULL,
  `janken_id` CHAR(36) NOT NULL,
  `player_id` CHAR(36) NOT NULL,
  `hand` INT UNSIGNED NOT NULL,
  `result` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `player_id` (`player_id` ASC),
  INDEX `janken_id` (`janken_id` ASC),
  CONSTRAINT `fk_janken_id`
    FOREIGN KEY (`janken_id`)
    REFERENCES `jankens` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_player_id`
    FOREIGN KEY (`player_id`)
    REFERENCES `players` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

INSERT INTO `players` (`id`, `name`) VALUES
(1, 'Alice'),
(2, 'Bob');
