# 包索引

本文档按包结构组织所有类，提供代码库架构的概览。

---

## Root Package (com.shatteredpixel.shatteredpixeldungeon)

Core game management classes.

| Class | Type | Purpose |
|-------|------|---------|
| Assets | class | Asset loading and management (sprites, sounds, music) |
| Badges | class | Achievement system tracking and display |
| Bones | class | Handles hero remains on death |
| Challenges | class | Game challenge modifiers (On Diet, Into Darkness, etc.) |
| Chrome | class | UI chrome/visual styling components |
| Dungeon | class | Main game state manager - core of the game |
| GamesInProgress | class | Save slot management |
| QuickSlot | class | Quick action bar system |
| Rankings | class | Leaderboard and score tracking |
| SPDAction | class | Input action mappings |
| SPDSettings | class | Game settings persistence |
| ShatteredPixelDungeon | class | Main application entry point |
| Statistics | class | Game statistics tracking |

---

## actors/

Turn-based entity system with scheduling.

| Class | Type | Purpose |
|-------|------|---------|
| Actor | abstract | Base class for all time-based entities, handles turn scheduling |
| Char | abstract | Character entity with HP, buffs, and combat capabilities |

---

## actors/blobs/

Area-of-effect gas and hazard system.

| Class | Type | Purpose |
|-------|------|---------|
| Alchemy | class | Alchemy pot catalyst blob |
| Blizzard | class | Freezing storm blob |
| Blob | abstract | Base class for all area effects |
| ConfusionGas | class | Confusion-inducing gas |
| CorrosiveGas | class | Acid damage gas |
| Electricity | class | Electrical hazard blob |
| Fire | class | Spreading fire blob |
| Foliage | class | Grass/hiding blob |
| Freezing | class | Ice formation blob |
| GooWarn | class | Goo boss warning indicator |
| Inferno | class | Intense fire blob |
| ParalyticGas | class | Stun gas |
| Regrowth | class | Plant growth blob |
| SacrificialFire | class | Sacrifice mechanic blob |
| SmokeScreen | class | Stealth smoke blob |
| StenchGas | class | Caustic gas variant |
| StormCloud | class | Weather effect blob |
| ToxicGas | class | Poison gas |
| VaultFlameTraps | class | Vault hazard |
| WaterOfAwareness | class | Well of identification |
| WaterOfHealth | class | Well of healing |
| Web | class | Spider web blob |
| WellWater | abstract | Base for magical wells |

---

## actors/buffs/

Status effect system (buffs and debuffs).

| Class | Type | Purpose |
|-------|------|---------|
| Adrenaline | class | Attack speed boost |
| AdrenalineSurge | class | Temporary strength boost |
| AllyBuff | abstract | Buffs applied by allies |
| Amok | class | Mind control debuff |
| ArcaneArmor | class | Magic damage reduction |
| ArtifactRecharge | class | Artifact charge boost |
| AscensionChallenge | class | Challenge mode tracker |
| Awareness | class | Reveal surroundings |
| Barkskin | class | Nature armor buff |
| Barrier | class | Shield HP buff |
| Berserk | class | Berserker rage state |
| Bleeding | class | Damage over time |
| Bless | class | Accuracy/dodge boost |
| Blindness | class | Vision reduction debuff |
| BlobImmunity | class | Gas immunity |
| Buff | abstract | Base class for all status effects |
| Burning | class | Fire damage debuff |
| ChampionEnemy | class | Elite enemy modifier |
| Charm | class | Mind control debuff |
| Chill | class | Slow debuff from cold |
| Combo | class | Gladiator combo tracker |
| Corrosion | class | Acid damage debuff |
| Corruption | class | Enemy conversion buff |
| CounterBuff | class | Counter tracking |
| Cripple | class | Movement debuff |
| Daze | class | Accuracy debuff |
| Degrade | class | Equipment weakening |
| Doom | class | Guaranteed death |
| Dread | class | Fear debuff |
| Drowsy | class | Sleep inducer |
| EnhancedRings | class | Ring power boost |
| FireImbue | class | Fire damage buff |
| FlavourBuff | class | Minor visual buff |
| Foresight | class | Trap reveal buff |
| Frost | class | Frozen state |
| FrostImbue | class | Cold damage buff |
| Fury | class | Low HP rage |
| GravityChaosTracker | class | Gravity effect |
| GreaterHaste | class | Major speed boost |
| Haste | class | Speed buff |
| Healing | class | HP regeneration |
| HeroDisguise | class | Appearance change |
| Hex | class | Accuracy debuff |
| HoldFast | class | Warrior stance |
| Hunger | class | Starvation tracker |
| Invisibility | class | Stealth buff |
| Invulnerability | class | Damage immunity |
| Levitation | class | Flight buff |
| LifeLink | class | Shared damage |
| Light | class | Illumination |
| LockedFloor | class | Floor lock tracker |
| LostInventory | class | Inventory loss |
| MagicImmune | class | Spell immunity |
| MagicalSight | class | Enhanced vision |
| MagicalSleep | class | Deep sleep |
| MindVision | class | See enemies |
| Momentum | class | Gladiator momentum |
| MonkEnergy | class | Monk ability resource |
| Ooze | class | Corrosive debuff |
| Paralysis | class | Stun debuff |
| PhysicalEmpower | class | Damage boost |
| PinCushion | class | Stuck projectiles |
| Poison | class | Damage over time |
| Preparation | class | Assassin prep |
| PrismaticGuard | class | Guardian ally |
| Recharging | class | Wand charge boost |
| Regeneration | class | HP regen |
| RevealedArea | class | Map reveal |
| Roots | class | Root debuff |
| ScrollEmpower | class | Scroll boost |
| Shadows | class | Stealth buff |
| ShieldBuff | abstract | Shield base class |
| Sleep | class | Sleep state |
| Slow | class | Speed reduction |
| SnipersMark | class | Huntress mark |
| SoulMark | class | Lifesteal marker |
| Speed | class | Movement speed |
| Stamina | class | Stamina buff |
| SuperNovaTracker | class | Special tracker |
| Terror | class | Fear debuff |
| TimeStasis | class | Time freeze |
| ToxicImbue | class | Poison damage buff |
| Vertigo | class | Movement confusion |
| Vulnerable | class | Damage increase debuff |
| WandEmpower | class | Wand damage boost |
| Weakness | class | Damage reduction |
| WellFed | class | Food satisfaction |

---

## actors/hero/

Player character system.

| Class | Type | Purpose |
|-------|------|---------|
| Belongings | class | Hero inventory management |
| Hero | class | Main player character - central hero class |
| HeroAction | class | Action type enumeration |
| HeroClass | enum | Character classes (Warrior, Mage, Rogue, Huntress, Duelist, Cleric) |
| HeroSubClass | enum | Subclass specializations |
| Talent | class | Talent tree system |

---

## actors/hero/abilities/

Armor-based special abilities.

| Class | Type | Purpose |
|-------|------|---------|
| ArmorAbility | abstract | Base class for armor abilities |
| Ratmogrify | class | Special transformation ability |

### abilities/cleric/
| Class | Type | Purpose |
|-------|------|---------|
| AscendedForm | class | Divine transformation |
| PowerOfMany | class | Ally enhancement |
| Trinity | class | Triple effect ability |

### abilities/duelist/
| Class | Type | Purpose |
|-------|------|---------|
| Challenge | class | Duel challenge ability |
| ElementalStrike | class | Element-infused attack |
| Feint | class | Deceptive movement |

### abilities/huntress/
| Class | Type | Purpose |
|-------|------|---------|
| NaturesPower | class | Nature enhancement |
| SpectralBlades | class | Ghost blade attack |
| SpiritHawk | class | Summon hawk ally |

### abilities/mage/
| Class | Type | Purpose |
|-------|------|---------|
| ElementalBlast | class | Area element attack |
| WarpBeacon | class | Teleport marker |
| WildMagic | class | Random wand effect |

### abilities/rogue/
| Class | Type | Purpose |
|-------|------|---------|
| DeathMark | class | Mark for death |
| ShadowClone | class | Create shadow clone |
| SmokeBomb | class | Escape ability |

### abilities/warrior/
| Class | Type | Purpose |
|-------|------|---------|
| Endure | class | Damage reduction |
| HeroicLeap | class | Jump attack |
| Shockwave | class | Area stun |

---

## actors/hero/spells/

Cleric spell system.

| Class | Type | Purpose |
|-------|------|---------|
| AuraOfProtection | class | Protective aura |
| BeamingRay | class | Light beam attack |
| BlessSpell | class | Blessing spell |
| BodyForm | class | Physical transformation |
| Cleanse | class | Remove debuffs |
| ClericSpell | abstract | Base cleric spell |
| DivineIntervention | class | Divine protection |
| DivineSense | class | Detection spell |
| Flash | class | Blinding light |
| GuidingLight | class | Light guidance |
| HallowedGround | class | Consecrated area |
| HolyIntuition | class | Intuition spell |
| HolyLance | class | Holy projectile |
| HolyWard | class | Protective ward |
| HolyWeapon | class | Weapon blessing |
| InventoryClericSpell | abstract | Inventory-affecting spell |
| Judgement | class | Judgement spell |
| LayOnHands | class | Healing touch |
| LifeLinkSpell | class | Life sharing |
| MindForm | class | Mental transformation |
| MnemonicPrayer | class | Memory spell |
| Radiance | class | Radiant light |
| RecallInscription | class | Recall glyph |
| ShieldOfLight | class | Light shield |
| Smite | class | Holy attack |
| SpiritForm | class | Spirit transformation |
| Stasis | class | Time freeze |
| Sunray | class | Sun beam |
| TargetedClericSpell | abstract | Targeted spell base |
| WallOfLight | class | Light barrier |

---

## actors/mobs/

Enemy entity system.

| Class | Type | Purpose |
|-------|------|---------|
| Acidic | class | Acid-dripping enemy |
| Albino | class | White rat variant |
| ArmoredBrute | class | Armored dwarf |
| ArmoredStatue | class | Armored animated armor |
| Bandit | class | Stealing enemy |
| Bat | class | Flying sewer enemy |
| Bee | class | Neutral bee ally/enemy |
| Brute | class | Strong dwarf |
| CausticSlime | class | Acid slime |
| Crab | class | Armored sewer enemy |
| CrystalGuardian | class | Crystal protector |
| CrystalMimic | class | Crystal monster |
| CrystalSpire | class | Crystal structure |
| CrystalWisp | class | Floating crystal |
| DM100 | class | Dwarf machine |
| DM200 | class | Heavy dwarf machine |
| DM201 | class | Upgraded machine |
| DM300 | class | Caves boss machine |
| DelayedRockFall | class | Falling rock hazard |
| DemonSpawner | class | Demon generator |
| DwarfKing | class | City boss |
| EbonyMimic | class | High-tier mimic |
| Elemental | class | Elemental enemy type |
| Eye | class | Demon halls enemy |
| FetidRat | class | Quest mini-boss |
| FungalCore | class | Fungus boss |
| FungalSentry | class | Fungus guardian |
| FungalSpinner | class | Fungus spider |
| Ghoul | class | Undead dwarf |
| Gnoll | class | Basic gnoll |
| GnollExile | class | Quest boss |
| GnollGeomancer | class | Gnoll boss |
| GnollGuard | class | Shield gnoll |
| GnollSapper | class | Explosive gnoll |
| GnollTrickster | class | Quest mini-boss |
| GoldenMimic | class | High-tier mimic |
| Golem | class | Construct enemy |
| Goo | class | Sewer boss |
| GreatCrab | class | Quest mini-boss |
| Guard | class | Prison enemy |
| HermitCrab | class | Armored crab |
| Mimic | class | Chest monster |
| Mob | abstract | Base class for all enemies |
| MobSpawner | class | Enemy spawner |
| Monk | class | Demon halls enemy |
| Necromancer | class | Skeleton summoner |
| PhantomPiranha | class | Ghost fish |
| Piranha | class | Water enemy |
| Pylon | class | DM-300 support |
| Rat | class | Basic sewer enemy |
| RipperDemon | class | Demon halls enemy |
| RotHeart | class | Rot garden boss |
| RotLasher | class | Rot garden enemy |
| Scorpio | class | Demon halls enemy |
| Senior | class | Upgraded monk |
| Shaman | class | Magic gnoll |
| Skeleton | class | Bone throwing enemy |
| Slime | class | Splitting enemy |
| Snake | class | Dodging sewer enemy |
| SpectralNecromancer | class | Ghost summoner |
| Spinner | class | Web-spinning enemy |
| Statue | class | Animated armor |
| Succubus | class | Charm enemy |
| Swarm | class | Fly swarm |
| Tengu | class | Prison boss |
| Thief | class | Stealing enemy |
| TormentedSpirit | class | Quest enemy |
| VaultMob | class | Vault enemy base |
| VaultRat | class | Vault enemy |
| Warlock | class | Magic dwarf |
| Wraith | class | Ghost enemy |
| YogDzewa | class | Final boss |
| YogFist | class | Yog minions |

---

## actors/mobs/npcs/

Non-player characters.

| Class | Type | Purpose |
|-------|------|---------|
| Blacksmith | class | Upgrade NPC |
| DirectableAlly | class | Player-controlled ally |
| Ghost | class | Quest NPC |
| Imp | class | Quest NPC |
| ImpShopkeeper | class | Shop NPC |
| MirrorImage | class | Rogue clone |
| NPC | abstract | Base NPC class |
| PrismaticImage | class | Guardian ally |
| RatKing | class | Quest NPC |
| Sheep | class | Passive animal |
| Shopkeeper | class | Shop NPC |
| VaultLaser | class | Vault hazard |
| VaultSentry | class | Vault guard |
| Wandmaker | class | Quest NPC |

---

## effects/

Visual effects system.

| Class | Type | Purpose |
|-------|------|---------|
| BadgeBanner | class | Achievement display |
| BannerSprites | class | Banner graphics |
| Beam | class | Beam visual effect |
| BlobEmitter | class | Gas particle emitter |
| CellEmitter | class | Cell-based particles |
| Chains | class | Chain visual |
| CheckedCell | class | Targeting indicator |
| CircleArc | class | Circular arc |
| DarkBlock | class | Dark visual block |
| Degradation | class | Degradation effect |
| Effects | class | Effect utilities |
| EmoIcon | class | Emotion icons |
| Enchanting | class | Enchant visual |
| Fireball | class | Fireball effect |
| Flare | class | Bright flare |
| FloatingText | class | Damage numbers |
| GlowBlock | class | Glowing block |
| IceBlock | class | Ice visual |
| Identification | class | ID sparkle effect |
| Lightning | class | Lightning bolt |
| MagicMissile | class | Magic projectile |
| Pushing | class | Push animation |
| Ripple | class | Water ripple |
| ShadowBox | class | Shadow effect |
| ShieldHalo | class | Shield glow |
| Speck | class | Particle speck |
| SpellSprite | class | Spell visual |
| Splash | class | Liquid splash |
| Surprise | class | Surprise indicator |
| Swap | class | Position swap |
| TargetedCell | class | Target indicator |
| TorchHalo | class | Torch light |
| Transmuting | class | Transform effect |
| Wound | class | Damage visual |

---

## effects/particles/

Particle system implementations.

| Class | Type | Purpose |
|-------|------|---------|
| BlastParticle | class | Explosion particle |
| BloodParticle | class | Blood particle |
| ChallengeParticle | class | Challenge indicator |
| CorrosionParticle | class | Acid particle |
| EarthParticle | class | Earth magic particle |
| ElmoParticle | class | Fire particle |
| EnergyParticle | class | Energy particle |
| FlameParticle | class | Flame particle |
| FlowParticle | class | Water flow particle |
| LeafParticle | class | Nature particle |
| PitfallParticle | class | Pit particle |
| PoisonParticle | class | Poison particle |
| PurpleParticle | class | Magic particle |
| RainbowParticle | class | Rainbow particle |
| SacrificialParticle | class | Sacrifice particle |
| ShadowParticle | class | Shadow particle |
| ShaftParticle | class | Light shaft particle |
| SmokeParticle | class | Smoke particle |
| SnowParticle | class | Snow particle |
| SparkParticle | class | Spark particle |
| SpectralWallParticle | class | Ghost wall particle |
| WebParticle | class | Web particle |
| WindParticle | class | Wind particle |
| WoolParticle | class | Wool particle |

---

## items/

Item system base classes.

| Class | Type | Purpose |
|-------|------|---------|
| Amulet | class | Victory item |
| Ankh | class | Resurrection item |
| ArcaneResin | class | Wand upgrade material |
| BrokenSeal | class | Warrior starting item |
| Dewdrop | class | Healing dew drop |
| EnergyCrystal | class | Energy currency |
| EquipableItem | abstract | Base for equipment |
| Generator | class | Item generation |
| Gold | class | Currency |
| Heap | class | Item pile on ground |
| Honeypot | class | Mimic bait |
| Item | abstract | Base class for all items |
| ItemStatusHandler | class | ID/curse handling |
| KindOfWeapon | abstract | Weapon interface |
| KindofMisc | abstract | Misc equipment |
| KingsCrown | class | Subclass unlock |
| LiquidMetal | class | Item repair |
| LostBackpack | class | Death recovery |
| Recipe | abstract | Alchemy recipe |
| Stylus | class | Glyph inscriber |
| TengusMask | class | Subclass choice |
| Torch | class | Light source |
| Waterskin | class | Liquid container |

---

## items/armor/

Armor equipment system.

| Class | Type | Purpose |
|-------|------|---------|
| Armor | abstract | Base armor class |
| ClassArmor | abstract | Class-specific armor |
| ClericArmor | class | Cleric armor |
| ClothArmor | class | Tier 1 armor |
| DuelistArmor | class | Duelist armor |
| HuntressArmor | class | Huntress armor |
| LeatherArmor | class | Tier 2 armor |
| MageArmor | class | Mage armor |
| MailArmor | class | Tier 3 armor |
| PlateArmor | class | Tier 5 armor |
| RogueArmor | class | Rogue armor |
| ScaleArmor | class | Tier 4 armor |
| WarriorArmor | class | Warrior armor |

### armor/curses/
| Class | Type | Purpose |
|-------|------|---------|
| AntiEntropy | class | Cold armor curse |
| Bulk | class | Size armor curse |
| Corrosion | class | Acid armor curse |
| Displacement | class | Teleport curse |
| Metabolism | class | Hunger curse |
| Multiplicity | class | Clone curse |
| Overgrowth | class | Nature curse |
| Stench | class | Gas curse |

### armor/glyphs/
| Class | Type | Purpose |
|-------|------|---------|
| Affection | class | Charm glyph |
| AntiMagic | class | Magic resist glyph |
| Brimstone | class | Fire glyph |
| Camouflage | class | Stealth glyph |
| Entanglement | class | Root glyph |
| Flow | class | Speed glyph |
| Obfuscation | class | Evasion glyph |
| Potential | class | Charge glyph |
| Repulsion | class | Knockback glyph |
| Stone | class | Stone glyph |
| Swiftness | class | Speed glyph |
| Thorns | class | Reflect glyph |
| Viscosity | class | Delay glyph |

---

## items/artifacts/

Unique special items.

| Class | Type | Purpose |
|-------|------|---------|
| AlchemistsToolkit | class | Alchemy artifact |
| Artifact | abstract | Base artifact class |
| CapeOfThorns | class | Reflect artifact |
| ChaliceOfBlood | class | HP-cost artifact |
| CloakOfShadows | class | Rogue invisibility |
| DriedRose | class | Ghost ally artifact |
| EtherealChains | class | Pulling artifact |
| HolyTome | class | Cleric artifact |
| HornOfPlenty | class | Food artifact |
| LloydsBeacon | class | Teleport artifact |
| MasterThievesArmband | class | Gold artifact |
| SandalsOfNature | class | Nature artifact |
| SkeletonKey | class | Master key |
| TalismanOfForesight | class | Trap reveal artifact |
| TimekeepersHourglass | class | Time stop artifact |
| UnstableSpellbook | class | Random scroll artifact |

---

## items/bags/

Inventory containers.

| Class | Type | Purpose |
|-------|------|---------|
| Bag | abstract | Base container class |
| MagicalHolster | class | Wand container |
| PotionBandolier | class | Potion container |
| ScrollHolder | class | Scroll container |
| VelvetPouch | class | Seed/runestone container |

---

## items/bombs/

Explosive items.

| Class | Type | Purpose |
|-------|------|---------|
| ArcaneBomb | class | Magic explosive |
| Bomb | class | Base bomb class |
| Firebomb | class | Fire explosive |
| FlashBangBomb | class | Stun explosive |
| FrostBomb | class | Ice explosive |
| HolyBomb | class | Anti-undead explosive |
| Noisemaker | class | Alert explosive |
| RegrowthBomb | class | Nature explosive |
| ShrapnelBomb | class | Shrapnel explosive |
| SmokeBomb | class | Smoke explosive |
| WoollyBomb | class | Sheep explosive |

---

## items/food/

Consumable food items.

| Class | Type | Purpose |
|-------|------|---------|
| Berry | class | Small food |
| Blandfruit | class | Alchemy fruit |
| ChargrilledMeat | class | Cooked meat |
| Food | abstract | Base food class |
| FrozenCarpaccio | class | Frozen meat |
| MeatPie | class | Large food |
| MysteryMeat | class | Risky food |
| Pasty | class | Special food |
| PhantomMeat | class | Special food |
| SmallRation | class | Small ration |
| StewedMeat | class | Cooked meat |
| SupplyRation | class | Ration |

---

## items/keys/

Key items for locked doors.

| Class | Type | Purpose |
|-------|------|---------|
| CrystalKey | class | Crystal lock key |
| GoldenKey | class | Golden chest key |
| IronKey | class | Standard key |
| Key | abstract | Base key class |
| WornKey | class | Old key |

---

## items/potions/

Consumable potions.

| Class | Type | Purpose |
|-------|------|---------|
| Potion | abstract | Base potion class |
| PotionOfExperience | class | XP potion |
| PotionOfFrost | class | Ice potion |
| PotionOfHaste | class | Speed potion |
| PotionOfHealing | class | Healing potion |
| PotionOfInvisibility | class | Stealth potion |
| PotionOfLevitation | class | Flight potion |
| PotionOfLiquidFlame | class | Fire potion |
| PotionOfMindVision | class | Vision potion |
| PotionOfParalyticGas | class | Stun potion |
| PotionOfPurity | class | Cleansing potion |
| PotionOfStrength | class | Strength potion |
| PotionOfToxicGas | class | Poison potion |

### potions/brews/
| Class | Type | Purpose |
|-------|------|---------|
| AquaBrew | class | Water brew |
| BlizzardBrew | class | Ice brew |
| Brew | abstract | Base brew class |
| CausticBrew | class | Acid brew |
| InfernalBrew | class | Fire brew |
| ShockingBrew | class | Lightning brew |
| UnstableBrew | class | Random brew |

### potions/elixirs/
| Class | Type | Purpose |
|-------|------|---------|
| Elixir | abstract | Base elixir class |
| ElixirOfAquaticRejuvenation | class | Water healing |
| ElixirOfArcaneArmor | class | Magic armor |
| ElixirOfDragonsBlood | class | Fire resistance |
| ElixirOfFeatherFall | class | Fall immunity |
| ElixirOfHoneyedHealing | class | Healing |
| ElixirOfIcyTouch | class | Cold damage |
| ElixirOfMight | class | Strength |
| ElixirOfToxicEssence | class | Poison immunity |

### potions/exotic/
| Class | Type | Purpose |
|-------|------|---------|
| ExoticPotion | abstract | Upgraded potion base |
| PotionOfCleansing | class | Immunity potion |
| PotionOfCorrosiveGas | class | Acid potion |
| PotionOfDivineInspiration | class | Talent potion |
| PotionOfDragonsBreath | class | Fire breath potion |
| PotionOfEarthenArmor | class | Earth armor potion |
| PotionOfMagicalSight | class | Vision potion |
| PotionOfMastery | class | Skill potion |
| PotionOfShielding | class | Shield potion |
| PotionOfShroudingFog | class | Fog potion |
| PotionOfSnapFreeze | class | Freeze potion |
| PotionOfStamina | class | Stamina potion |
| PotionOfStormClouds | class | Weather potion |

---

## items/rings/

Ring equipment.

| Class | Type | Purpose |
|-------|------|---------|
| Ring | abstract | Base ring class |
| RingOfAccuracy | class | Accuracy ring |
| RingOfArcana | class | Magic ring |
| RingOfElements | class | Resistance ring |
| RingOfEnergy | class | Charge ring |
| RingOfEvasion | class | Dodge ring |
| RingOfForce | class | Damage ring |
| RingOfFuror | class | Attack speed ring |
| RingOfHaste | class | Movement ring |
| RingOfMight | class | Strength ring |
| RingOfSharpshooting | class | Range ring |
| RingOfTenacity | class | Damage reduction ring |
| RingOfWealth | class | Loot ring |

---

## items/scrolls/

Consumable scrolls.

| Class | Type | Purpose |
|-------|------|---------|
| InventoryScroll | abstract | Scroll affecting inventory |
| Scroll | abstract | Base scroll class |
| ScrollOfIdentify | class | ID scroll |
| ScrollOfLullaby | class | Sleep scroll |
| ScrollOfMagicMapping | class | Map scroll |
| ScrollOfMirrorImage | class | Clone scroll |
| ScrollOfRage | class | Aggro scroll |
| ScrollOfRecharging | class | Charge scroll |
| ScrollOfRemoveCurse | class | Curse removal |
| ScrollOfRetribution | class | Damage scroll |
| ScrollOfTeleportation | class | Teleport scroll |
| ScrollOfTerror | class | Fear scroll |
| ScrollOfTransmutation | class | Transform scroll |
| ScrollOfUpgrade | class | Upgrade scroll |

### scrolls/exotic/
| Class | Type | Purpose |
|-------|------|---------|
| ExoticScroll | abstract | Upgraded scroll base |
| ScrollOfAntiMagic | class | Magic immunity |
| ScrollOfChallenge | class | Challenge area |
| ScrollOfDivination | class | Identify |
| ScrollOfDread | class | Mass fear |
| ScrollOfEnchantment | class | Enchant item |
| ScrollOfForesight | class | Trap reveal |
| ScrollOfMetamorphosis | class | Talent reset |
| ScrollOfMysticalEnergy | class | Charge boost |
| ScrollOfPassage | class | Safe teleport |
| ScrollOfPrismaticImage | class | Guardian summon |
| ScrollOfPsionicBlast | class | Mind damage |
| ScrollOfSirensSong | class | Charm enemies |

---

## items/spells/

Craftable spell items.

| Class | Type | Purpose |
|-------|------|---------|
| Alchemize | class | Convert items |
| BeaconOfReturning | class | Teleport marker |
| CurseInfusion | class | Curse equipment |
| InventorySpell | abstract | Inventory spell base |
| MagicalInfusion | class | Upgrade spell |
| PhaseShift | class | Teleport spell |
| ReclaimTrap | class | Trap reuse |
| Recycle | class | Transform item |
| Spell | abstract | Base spell class |
| SummonElemental | class | Summon ally |
| TargetedSpell | abstract | Targeted spell base |
| TelekineticGrab | class | Remote pickup |
| UnstableSpell | class | Random effect |
| WildEnergy | class | Charge spell |

---

## items/stones/

Runestone items.

| Class | Type | Purpose |
|-------|------|---------|
| InventoryStone | abstract | Inventory stone base |
| Runestone | abstract | Base runestone class |
| StoneOfAggression | class | Aggro stone |
| StoneOfAugmentation | class | Stat boost stone |
| StoneOfBlast | class | Explosion stone |
| StoneOfBlink | class | Teleport stone |
| StoneOfClairvoyance | class | Vision stone |
| StoneOfDeepSleep | class | Sleep stone |
| StoneOfDetectMagic | class | Magic detect stone |
| StoneOfEnchantment | class | Enchant stone |
| StoneOfFear | class | Fear stone |
| StoneOfFlock | class | Sheep stone |
| StoneOfIntuition | class | ID stone |
| StoneOfShock | class | Lightning stone |

---

## items/trinkets/

Trinket equipment items.

| Class | Type | Purpose |
|-------|------|---------|
| ChaoticCenser | class | Random spell trinket |
| CrackedSpyglass | class | Vision trinket |
| DimensionalSundial | class | Time trinket |
| ExoticCrystals | class | Exotic drop trinket |
| EyeOfNewt | class | Vision trinket |
| FerretTuft | class | Speed trinket |
| MimicTooth | class | Mimic trinket |
| MossyClump | class | Grass trinket |
| ParchmentScrap | class | Scroll trinket |
| PetrifiedSeed | class | Seed trinket |
| RatSkull | class | Enemy spawn trinket |
| SaltCube | class | Preservation trinket |
| ShardOfOblivion | class | Special trinket |
| ThirteenLeafClover | class | Luck trinket |
| TrapMechanism | class | Trap trinket |
| Trinket | abstract | Base trinket class |
| TrinketCatalyst | class | Trinket creation |
| VialOfBlood | class | Blood trinket |
| WondrousResin | class | Wand trinket |

---

## items/wands/

Wand weapons.

| Class | Type | Purpose |
|-------|------|---------|
| CursedWand | class | Curse effects |
| DamageWand | abstract | Damage wand base |
| Wand | abstract | Base wand class |
| WandOfBlastWave | class | Push wand |
| WandOfCorrosion | class | Acid wand |
| WandOfCorruption | class | Conversion wand |
| WandOfDisintegration | class | Pierce wand |
| WandOfFireblast | class | Fire wand |
| WandOfFrost | class | Ice wand |
| WandOfLightning | class | Lightning wand |
| WandOfLivingEarth | class | Earth guardian wand |
| WandOfMagicMissile | class | Basic damage wand |
| WandOfPrismaticLight | class | Light wand |
| WandOfRegrowth | class | Plant wand |
| WandOfTransfusion | class | Blood wand |
| WandOfWarding | class | Ward wand |

---

## items/weapon/

Weapon system.

| Class | Type | Purpose |
|-------|------|---------|
| SpiritBow | class | Huntress signature weapon |
| Weapon | abstract | Base weapon class |

### weapon/melee/
| Class | Type | Purpose |
|-------|------|---------|
| AssassinsBlade | class | Assassin weapon |
| BattleAxe | class | Heavy axe |
| Crossbow | class | Hybrid weapon |
| Cudgel | class | Blunt weapon |
| Dagger | class | Fast weapon |
| Dirk | class | Medium dagger |
| Flail | class | Unpredictable weapon |
| Gauntlet | class | Fist weapon |
| Glaive | class | Polearm |
| Gloves | class | Fist weapon |
| Greataxe | class | Two-handed axe |
| Greatshield | class | Defensive weapon |
| Greatsword | class | Two-handed sword |
| HandAxe | class | Basic axe |
| Katana | class | Fast sword |
| Longsword | class | Medium sword |
| Mace | class | Blunt weapon |
| MagesStaff | class | Mage weapon |
| MeleeWeapon | abstract | Melee weapon base |
| Quarterstaff | class | Staff weapon |
| Rapier | class | Fencing weapon |
| RoundShield | class | Defensive weapon |
| RunicBlade | class | Special weapon |
| Sai | class | Dual blade |
| Scimitar | class | Curved sword |
| Shortsword | class | Light sword |
| Sickle | class | Farming weapon |
| Spear | class | Polearm |
| Sword | class | Standard sword |
| WarHammer | class | Heavy weapon |
| WarScythe | class | Scythe weapon |
| Whip | class | Long range weapon |
| WornShortsword | class | Basic weapon |

### weapon/missiles/
| Class | Type | Purpose |
|-------|------|---------|
| Bolas | class | Thrown weapon |
| FishingSpear | class | Thrown weapon |
| ForceCube | class | Area weapon |
| HeavyBoomerang | class | Returning weapon |
| Javelin | class | Thrown spear |
| Kunai | class | Throwing knife |
| MissileWeapon | abstract | Thrown weapon base |
| Shuriken | class | Throwing star |
| ThrowingClub | class | Thrown weapon |
| ThrowingHammer | class | Thrown weapon |
| ThrowingKnife | class | Thrown weapon |
| ThrowingSpear | class | Thrown weapon |
| ThrowingSpike | class | Thrown weapon |
| ThrowingStone | class | Thrown weapon |
| Tomahawk | class | Thrown axe |
| Trident | class | Thrown polearm |

### weapon/missiles/darts/
| Class | Type | Purpose |
|-------|------|---------|
| AdrenalineDart | class | Speed dart |
| BlindingDart | class | Blind dart |
| ChillingDart | class | Cold dart |
| CleansingDart | class | Cleanse dart |
| Dart | class | Base dart class |
| DisplacingDart | class | Teleport dart |
| HealingDart | class | Heal dart |
| HolyDart | class | Holy dart |
| IncendiaryDart | class | Fire dart |
| ParalyticDart | class | Stun dart |
| PoisonDart | class | Poison dart |
| RotDart | class | Decay dart |
| ShockingDart | class | Lightning dart |
| TippedDart | abstract | Enhanced dart base |

### weapon/enchantments/
| Class | Type | Purpose |
|-------|------|---------|
| Blazing | class | Fire enchant |
| Blocking | class | Shield enchant |
| Blooming | class | Nature enchant |
| Chilling | class | Cold enchant |
| Corrupting | class | Convert enchant |
| Elastic | class | Knockback enchant |
| Grim | class | Death enchant |
| Kinetic | class | Stored damage enchant |
| Lucky | class | Double damage enchant |
| Projecting | class | Range enchant |
| Shocking | class | Lightning enchant |
| Unstable | class | Random enchant |
| Vampiric | class | Lifesteal enchant |

### weapon/curses/
| Class | Type | Purpose |
|-------|------|---------|
| Annoying | class | Sound curse |
| Dazzling | class | Blind curse |
| Displacing | class | Teleport curse |
| Explosive | class | Explosion curse |
| Friendly | class | Ally hit curse |
| Polarized | class | All-or-nothing curse |
| Sacrificial | class | Ally damage curse |
| Wayward | class | Miss curse |

---

## levels/

Dungeon level system.

| Class | Type | Purpose |
|-------|------|---------|
| CavesBossLevel | class | DM-300 arena |
| CavesLevel | class | Caves dungeon |
| CityBossLevel | class | Dwarf King arena |
| CityLevel | class | Metropolis dungeon |
| DeadEndLevel | class | Special dead-end |
| HallsBossLevel | class | Yog-Dzewa arena |
| HallsLevel | class | Demon halls dungeon |
| LastLevel | class | Final level |
| LastShopLevel | class | Final shop |
| Level | abstract | Base level class |
| MiningLevel | class | Mining level |
| Patch | class | Level patch generation |
| PrisonBossLevel | class | Tengu arena |
| PrisonLevel | class | Prison dungeon |
| RegularLevel | abstract | Standard level base |
| SewerBossLevel | class | Goo arena |
| SewerLevel | class | Sewer dungeon |
| Terrain | enum | Tile type enumeration |
| VaultLevel | class | Vault dungeon |

### levels/builders/
| Class | Type | Purpose |
|-------|------|---------|
| BranchesBuilder | class | Branching level builder |
| Builder | abstract | Level builder base |
| FigureEightBuilder | class | Figure-8 builder |
| GridBuilder | class | Grid level builder |
| LineBuilder | class | Linear builder |
| LoopBuilder | class | Loop level builder |
| RegularBuilder | class | Standard builder |

### levels/features/
| Class | Type | Purpose |
|-------|------|---------|
| Chasm | class | Pit hazard |
| Door | class | Door tile feature |
| HighGrass | class | Tall grass feature |
| LevelTransition | class | Level change point |
| Maze | class | Maze feature |

### levels/painters/
| Class | Type | Purpose |
|-------|------|---------|
| CavesPainter | class | Caves tile painter |
| CityPainter | class | City tile painter |
| HallsPainter | class | Halls tile painter |
| MiningLevelPainter | class | Mining painter |
| Painter | abstract | Tile painter base |
| PrisonPainter | class | Prison tile painter |
| RegularPainter | class | Standard painter |
| SewerPainter | class | Sewer tile painter |

### levels/rooms/
| Class | Type | Purpose |
|-------|------|---------|
| Room | abstract | Room base class |

---

## windows/

UI window system.

| Class | Type | Purpose |
|-------|------|---------|
| IconTitle | class | Icon with title |
| WndAlchemy | class | Alchemy window |
| WndBag | class | Inventory window |
| WndBadge | class | Badge window |
| WndBlacksmith | class | Blacksmith window |
| WndChallenges | class | Challenge window |
| WndChooseAbility | class | Ability choice window |
| WndChooseSubclass | class | Subclass window |
| WndClericSpells | class | Cleric spell window |
| WndCombo | class | Combo window |
| WndDailies | class | Daily challenge window |
| WndDocument | class | Document window |
| WndEnergizeItem | class | Energy convert window |
| WndError | class | Error window |
| WndGame | class | Game menu window |
| WndGameInProgress | class | Save slot window |
| WndHardNotification | class | Alert window |
| WndHero | class | Hero stats window |
| WndHeroInfo | class | Hero info window |
| WndImp | class | Imp window |
| WndInfoArmorAbility | class | Ability info window |
| WndInfoBuff | class | Buff info window |
| WndInfoCell | class | Tile info window |
| WndInfoItem | class | Item info window |
| WndInfoMob | class | Enemy info window |
| WndInfoPlant | class | Plant info window |
| WndInfoSubclass | class | Subclass info window |
| WndInfoTalent | class | Talent info window |
| WndInfoTrap | class | Trap info window |
| WndJournal | class | Journal window |
| WndJournalItem | class | Journal entry window |
| WndKeyBindings | class | Controls window |
| WndList | class | List window |
| WndMessage | class | Message window |
| WndMonkAbilities | class | Monk ability window |
| WndOptions | class | Options window |
| WndOptionsCondensed | class | Compact options window |
| WndQuest | class | Quest window |
| WndQuickBag | class | Quick bag window |
| WndRanking | class | Ranking window |
| WndResurrect | class | Resurrection window |
| WndSadGhost | class | Ghost quest window |
| WndScoreBreakdown | class | Score window |
| WndSettings | class | Settings window |
| WndStory | class | Story window |
| WndSupportPrompt | class | Support window |
| WndTabbed | class | Tabbed window base |
| WndTextInput | class | Text input window |
| WndTitledMessage | class | Titled message window |
| WndTradeItem | class | Shop window |
| WndUpgrade | class | Upgrade window |
| WndUseItem | class | Item use window |
| WndVictoryCongrats | class | Victory window |
| WndWandmaker | class | Wandmaker window |

---

## Engine Package (com.watabou.noosa)

Visual rendering engine.

| Class | Type | Purpose |
|-------|------|---------|
| BitmapText | class | Bitmap font text |
| Camera | class | Viewport camera |
| ColorBlock | class | Solid color block |
| Game | class | Game engine core |
| Gizmo | abstract | Base game object |
| Group | class | Visual container |
| Halo | class | Halo visual effect |
| Image | class | Sprite image |
| MovieClip | class | Animated sprite |
| NinePatch | class | 9-slice image |
| NoosaScript | class | Rendering shader |
| NoosaScriptNoLighting | class | Unlit shader |
| PointerArea | class | Touch/mouse area |
| PseudoPixel | class | Single pixel |
| RenderedText | class | System font text |
| Resizable | class | Resizable visual |
| Scene | class | Game scene base |
| ScrollArea | class | Scrollable area |
| SkinnedBlock | class | Tiled block |
| TextInput | class | Text input |
| TextureFilm | class | Sprite frames |
| Tilemap | class | Tile rendering |
| Visual | abstract | Base visual entity |

### noosa/particles/
| Class | Type | Purpose |
|-------|------|---------|
| BitmaskEmitter | class | Masked emitter |
| Emitter | class | Particle emitter |
| PixelParticle | class | Single pixel particle |

### noosa/tweeners/
| Class | Type | Purpose |
|-------|------|---------|
| AlphaTweener | class | Alpha animation |
| CameraScrollTweener | class | Camera scroll |
| Delayer | class | Animation delay |
| PosTweener | class | Position animation |
| ScaleTweener | class | Scale animation |
| Tweener | abstract | Animation base |

### noosa/audio/
| Class | Type | Purpose |
|-------|------|---------|
| Music | class | Music playback |
| Sample | class | Sound playback |

### noosa/ui/
| Class | Type | Purpose |
|-------|------|---------|
| Component | class | UI component base |
| Cursor | class | Mouse cursor |

---

## Utilities (com.watabou.utils)

| Class | Type | Purpose |
|-------|------|---------|
| BArray | class | Boolean array utilities |
| BitmapCache | class | Bitmap memory cache |
| BitmapFilm | class | Bitmap frames |
| Bundlable | interface | Serialization interface |
| Bundle | class | Data serialization |
| Callback | interface | Callback interface |
| ColorMath | class | Color math utilities |
| DeviceCompat | class | Device compatibility |
| FileUtils | class | File I/O utilities |
| GameMath | class | Math utilities |
| GameSettings | class | Settings storage |
| Graph | class | Graph algorithms |
| PathFinder | class | A* pathfinding |
| PlatformSupport | class | Platform abstraction |
| Point | class | 2D integer point |
| PointF | class | 2D float point |
| Random | class | Random numbers |
| Rect | class | Integer rectangle |
| RectF | class | Float rectangle |
| Reflection | class | Java reflection |
| Signal | class | Event system |
| SparseArray | class | Sparse array |

---

## Graphics (com.watabou.glwrap)

| Class | Type | Purpose |
|-------|------|---------|
| Attribute | class | Vertex attribute |
| Blending | class | Blend modes |
| Framebuffer | class | FBO |
| Matrix | class | Matrix math |
| Program | class | Shader program |
| Quad | class | Quad mesh |
| Renderbuffer | class | RBO |
| Shader | class | GLSL shader |
| Texture | class | Texture object |
| Uniform | class | Shader uniform |
| Vertexbuffer | class | VBO |

---

*Package Index generated for Shattered Pixel Dungeon - 1289 classes across 85 packages*