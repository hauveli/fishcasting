# .venv\Lib\site-packages\hexdoc_hexcasting\book\recipes.py
# .venv\Lib\site-packages\hexdoc_hexcasting\_templates\recipes\hexcasting\brainsweep.html.jinja
from abc import ABC, abstractmethod
from typing import Any, Literal, Self

from hexdoc.core import ResourceLocation
from hexdoc.minecraft.assets import ItemWithTexture, PNGTexture
from hexdoc.minecraft.i18n import I18n, LocalizedStr
from hexdoc.minecraft.recipe import ItemIngredient, ItemIngredientList, Recipe
from hexdoc.minecraft.recipe import ItemIngredientList, ItemResult, Recipe
from pydantic import Field, PrivateAttr, ValidationInfo, model_validator
from hexdoc_hexcasting.book.recipes import BrainsweepeeIngredient


class StruckByLightningIngredient(BrainsweepeeIngredient, type="fishcasting:struck_by_lightning_entity_types"):
    entityIn: ResourceLocation = Field(alias="entityIn")
    entityOut: ResourceLocation = Field(alias="entityOut")

    _nameIn: LocalizedStr = PrivateAttr()
    _textureIn: PNGTexture = PrivateAttr()

    _nameOut: LocalizedStr = PrivateAttr()
    _textureOut: PNGTexture = PrivateAttr()

    @property
    def nameIn(self):
        return self._nameIn

    @property
    def textureIn(self):
        return self._textureIn

    @property
    def nameOut(self):
        return self._nameOut

    @property
    def textureOut(self):
        return self._textureOut

    @model_validator(mode="after")
    def _get_textureIn(self, info: ValidationInfo) -> Self:
        assert info.context is not None
        i18n = I18n.of(info)

        self._name = i18n.localize_entity(self.entityIn)

        self._texture = PNGTexture.load_id(
            id="textures/entities" / self.entityIn + ".png",
            context=info.context,
        )

        return self

    @model_validator(mode="after")
    def _get_textureOut(self, info: ValidationInfo) -> Self:
        assert info.context is not None
        i18n = I18n.of(info)

        self._name = i18n.localize_entity(self.entityOut)

        self._texture = PNGTexture.load_id(
            id="textures/entities" / self.entityOut + ".png",
            context=info.context,
        )

        return self



class StruckByLightningRecipe(Recipe, ABC, type="fishcasting:struck_by_lightning"):
    exchange: StruckByLightningIngredient

    @property
    def input(self) -> Any:
        return self.exchange.entityIn

    @property
    def output(self) -> Any:
        return self.exchange.entityOut
