# .venv\Lib\site-packages\hexdoc_hexcasting\book\recipes.py
# .venv\Lib\site-packages\hexdoc_hexcasting\_templates\recipes\hexcasting\brainsweep.html.jinja

from hexdoc.patchouli.text import FormattingContext, FormatTree
from hexdoc.patchouli.page import EmptyPage, PageWithText, PageWithTitle
from hexdoc.core import IsVersion, ItemStack, ResourceLocation
from hexdoc.minecraft.i18n import I18n, LocalizedStr
from hexdoc_hexcasting.book.page.pages import BrainsweepPage
from hexdoc_fishcasting.recipes import StruckByLightningRecipe as LightningRecipe
from pydantic import Field, PrivateAttr, ValidationInfo, model_validator

class VoidsweepPage(BrainsweepPage, type="hexcasting:fishcasting/brainsweep_void"):
    pass

# I dont know why this works, my intuition would say it shouldn't but I don't understand python well enough so this is fine
class VoidsweepPage(BrainsweepPage, type="hexcasting:fishcasting/brainsweep_void_clueless"):
    pass

# todo: implement this thing here then reference it from the others
class LightningPage(PageWithTitle, type="hexcasting:fishcasting/struck_by_lightning"):
    recipe: LightningRecipe
    body: FormatTree = Field(alias="body")
    header: LocalizedStr = Field(alias="header")



class LightningAltPage(LightningPage, type="hexcasting:fishcasting/struck_by_lightning_hint"):
    pass


class LightningAltPage(LightningPage, type="hexcasting:fishcasting/struck_by_lightning_clueless"):
    pass

