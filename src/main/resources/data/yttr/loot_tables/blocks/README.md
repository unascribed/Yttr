# Wait where are all the loot tables
Yttr automatically makes blocks with the `yttr` namespace drop themselves if they do not have any
loot table assigned.

This directory only contains blocks which have some form of special drop logic, which can be
represented in a loot table in a way that does not end with me pulling my hair out. (Other kinds of
drops are handled via the SimpleLootBlock interface.)

See src/main/java/com/unascribed/yttr/mixin/convenience/MixinAbstractBlock
