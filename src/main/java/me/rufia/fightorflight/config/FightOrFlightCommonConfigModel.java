package me.rufia.fightorflight.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "fightorflight")
@Config(name = "fightorflight-common-config", wrapperName = "FightOrFlightCommonConfigs")
public class FightOrFlightCommonConfigModel {

    public boolean DO_POKEMON_ATTACK = true;
    //DO_POKEMON_ATTACK = BUILDER.comment("Do more aggressive Pokemon fight back when provoked?").define("do_pokemon_fight_back", true);
    public boolean DO_POKEMON_ATTACK_UNPROVOKED = true;
    //DO_POKEMON_ATTACK_UNPROVOKED = BUILDER.comment("Do especially aggressive Pokemon attack unprovoked?").define("do_pokemon_attack_unprovoked", true);
    public boolean DO_POKEMON_ATTACK_IN_BATTLE = false;
    //DO_POKEMON_ATTACK_IN_BATTLE = BUILDER.comment("Do aggro Pokemon wait for their target to finish any battles before attacking?").define("do_pokemon_attack_in_battle", true);

    public boolean DO_POKEMON_DEFEND_OWNER = true;
    //DO_POKEMON_DEFEND_OWNER = BUILDER.comment("Do player Pokemon defend their owners when they attack or are attacked by other mobs?").define("do_pokemon_defend_owners", true);
    public boolean DO_PLAYER_POKEMON_ATTACK_OTHER_PLAYERS = false;
    //DO_PLAYER_POKEMON_ATTACK_OTHER_PLAYERS = BUILDER.comment("Can player Pokemon target other players? (EXPERIMENTAL)").define("do_player_pokemon_attack_other_players", false);
    public boolean DO_PLAYER_POKEMON_ATTACK_OTHER_PLAYER_POKEMON = false;
    //DO_PLAYER_POKEMON_ATTACK_OTHER_PLAYER_POKEMON = BUILDER.comment("Can player Pokemon target other player's Pokemon? (EXPERIMENTAL)").define("do_player_pokemon_attack_other_player_pokemon", false);
    public boolean FORCE_WILD_BATTLE_ON_POKEMON_HURT = false;
    //FORCE_WILD_BATTLE_ON_POKEMON_HURT = BUILDER.comment("When a player owned Pokemon hurts or is hurt by a wild pokemon, should a pokemon battle be started?").define("force_wild_battle_on_pokemon_hurt", false);
    public boolean FORCE_PLAYER_BATTLE_ON_POKEMON_HURT = false;
    //FORCE_PLAYER_BATTLE_ON_POKEMON_HURT = BUILDER.comment("When a player owned Pokemon hurts or is hurt by another player's pokemon, should a pokemon battle be started? (EXPERIMENTAL)").define("force_player_battle_on_pokemon_hurt",false);

    public int MINIMUM_ATTACK_LEVEL = 5;
    //MINIMUM_ATTACK_LEVEL = BUILDER.comment("The minimum level a Pokemon needs to be to fight back when provoked.").defineInRange("min_fight_back_level", 5, 0, 100);
    public int MINIMUM_ATTACK_UNPROVOKED_LEVEL = 10;
    //MINIMUM_ATTACK_UNPROVOKED_LEVEL = BUILDER.comment("The minimum level a Pokemon needs to be to attack unprovoked.").defineInRange("min_attack_unprovoked_level", 10, 0, 100);




}
