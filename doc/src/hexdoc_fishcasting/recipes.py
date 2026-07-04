# .venv\Lib\site-packages\hexdoc_hexcasting\book\recipes.py
# .venv\Lib\site-packages\hexdoc_hexcasting\_templates\recipes\hexcasting\brainsweep.html.jinja
from abc import ABC, abstractmethod
from typing import Any, Literal, Self
from types import SimpleNamespace

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

    _nameZap: LocalizedStr = PrivateAttr()
    _textureZap: PNGTexture = PrivateAttr()

    @property
    def nameIn(self):
        return self._nameIn

    @property
    def textureIn(self):
        return self._textureIn

    @property
    def objectIn(self):
        return SimpleNamespace(
            name=self._nameIn,
            texture=self._textureIn,
        )

    @property
    def nameOut(self):
        return self._nameOut

    @property
    def textureOut(self):
        return self._textureOut

    @property
    def objectOut(self):
        return SimpleNamespace(
            name=self._nameOut,
            texture=self._textureOut,
        )

    @property
    def objectZap(self):
        return SimpleNamespace(
            name=self._nameZap,
            texture=self._textureZap,
        )



    @property
    def templateIn(self):
        # template_id is actually supposed to just be a string
        # but pydantic generics are hard :(
        return f"ingredients/fishcasting/struck_by_lightning/input"

    @property
    def templateOut(self):
        # template_id is actually supposed to just be a string
        # but pydantic generics are hard :(
        return f"ingredients/fishcasting/struck_by_lightning/output"

    @property
    def templateZap(self):
        # template_id is actually supposed to just be a string
        # but pydantic generics are hard :(
        return f"ingredients/fishcasting/struck_by_lightning/zap"


    @model_validator(mode="after")
    def _get_textureIn(self, info: ValidationInfo) -> Self:
        assert info.context is not None
        i18n = I18n.of(info)

        self._nameIn = i18n.localize_entity(self.entityIn)

        self._textureIn = PNGTexture.load_id(
            id="textures/entities" / self.entityIn + ".png",
            context=info.context,
        )

        return self

    @model_validator(mode="after")
    def _get_textureOut(self, info: ValidationInfo) -> Self:
        assert info.context is not None
        i18n = I18n.of(info)

        self._nameOut = i18n.localize_entity(self.entityOut)

        self._textureOut = PNGTexture.load_id(
            id="textures/entities" / self.entityOut + ".png",
            context=info.context,
        )

        return self

    @model_validator(mode="after")
    def _get_textureZap(self, info: ValidationInfo) -> Self:
        assert info.context is not None
        i18n = I18n.of(info)

        self._nameZap = i18n.localize_entity(ResourceLocation.from_str("minecraft:lightning_bolt"))

        self._textureZap = PNGTexture.load_id(
            id="textures/entities/lightning_bolt.png",
            context=info.context,
        )


class StruckByLightningRecipe(Recipe, ABC, type="fishcasting:struck_by_lightning"):
    exchange: StruckByLightningIngredient

    @property
    def input(self) -> Any:
        return self.exchange.entityIn

    @property
    def output(self) -> Any:
        return self.exchange.entityOut

    @property
    def inTex(self) -> Any:
        return self.exchange.textureIn

    @property
    def outTex(self) -> Any:
        return self.exchange.textureOut


    @property
    def inTemplate(self) -> Any:
        return self.exchange.templateIn

    @property
    def outTemplate(self) -> Any:
        return self.exchange.templateOut

    @property
    def zapTemplate(self) -> Any:
        return self.exchange.templateZap

    @property
    def inObject(self) -> Any:
        return self.exchange.objectIn

    @property
    def outObject(self) -> Any:
        return self.exchange.objectOut

    @property
    def zapObject(self) -> Any:
        return self.exchange.objectZap

