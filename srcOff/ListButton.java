package harmonised.pmmo.gui;

import harmonised.pmmo.config.JType;
import harmonised.pmmo.skills.Skill;
import harmonised.pmmo.util.XP;
import harmonised.pmmo.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ListButton extends GuiButton
{
    private final ResourceLocation items = XP.getResLoc( Reference.MOD_ID, "textures/gui/items.png" );
    private final ResourceLocation buttons = XP.getResLoc( Reference.MOD_ID, "textures/gui/buttons.png" );
//    private final Screen screen = new SkillsScreen( new TextComponentTranslation( "pmmo.potato" ));
    public int elementOne, elementTwo;
    public int offsetOne, offsetTwo;
    public double mobWidth, mobHeight, mobScale;
    public boolean unlocked = true;
    public ItemStack itemStack;
    public String regKey, title, buttonText;
    public List<String> text = new ArrayList<>();
    public List<String> tooltipText = new ArrayList<>();
    Entity testEntity = null;
    EntityLiving entity = null;
    ItemRenderer itemRenderer = Minecraft.getMinecraft().getItemRenderer();

    public ListButton( int id, int posX, int posY, int elementOne, int elementTwo, String regKey, JType jType, String buttonText, IPressable onPress )
    {
        super( id, posX, posY, 32, 32, "" );
        this.regKey = regKey;
        this.buttonText = buttonText;
        this.itemStack = new ItemStack( XP.getItem( regKey ) );
        this.elementOne = elementOne * 32;
        this.elementTwo = elementTwo * 32;

        if( ForgeRegistries.ENTITIES.containsKey( XP.getResLoc( regKey ) ) )
            testEntity = ForgeRegistries.ENTITIES.getValue( XP.getResLoc( regKey ) ).create( Minecraft.getMinecraft().world );

        if( testEntity instanceof EntityLiving )
            entity = (EntityLiving) testEntity;

        switch( jType )
        {
            case FISH_ENCHANT_POOL:
                this.title = new TextComponentTranslation( ForgeRegistries.ENCHANTMENTS.getValue( XP.getResLoc( regKey ) ).getTranslatedName( 1 ).replace( " I", "" ) ).getUnformattedText();
                break;

            case XP_VALUE_BREED:
            case XP_VALUE_TAME:
            case REQ_KILL:
                try
                {
                    this.title = new TextComponentTranslation( ForgeRegistries.ENTITIES.getValue( XP.getResLoc( regKey ) ).getName() ).getUnformattedText();
                }
                catch( Exception e )
                {
                    this.title = "No Name";
                }
                break;

//            case DIMENSION:
//                if( regKey.equals( "all_dimensions" ) )
//                    this.title = new TextComponentTranslation( "pmmo.allDimensions" ).getFormattedText();
//                else if( regKey.equals( "minecraft:overworld" ) || regKey.equals( "minecraft:the_nether" ) || regKey.equals( "minecraft:the_end" ) )
//                    this.title = new TextComponentTranslation( regKey ).getFormattedText();
//                else if( ForgeRegistries.MOD_DIMENSIONS.containsKey( XP.getResLoc( regKey ) ) )
//                    this.title = new TextComponentTranslation( ForgeRegistries.MOD_DIMENSIONS.getValue( XP.getResLoc( regKey ) ).getRegistryName().toString() ).getFormattedText();
//                break;
            //COUT GUI DIMENSIONS

            case STATS:
                this.title = new TextComponentTranslation( "pmmo." + regKey ).setStyle( XP.getSkillStyle(Skill.getSkill( regKey ) ) ).getFormattedText();
                break;

            case REQ_BIOME:
//                this.title = new TextComponentTranslation( ForgeRegistries.BIOMES.getValue( XP.getResLoc( regKey ) ).getTranslationKey() ).getUnformattedText();
                this.title = new TextComponentTranslation( regKey ).getUnformattedText();
                break;

            default:
                this.title = new TextComponentTranslation( itemStack.getDisplayName() ).getUnformattedText();
                break;
        }

        switch( regKey )
        {
            case "pmmo.otherCrafts":
            case "pmmo.otherAnimals":
            case "pmmo.otherPassiveMobs":
            case "pmmo.otherAggresiveMobs":
                this.title = new TextComponentTranslation( new TextComponentTranslation( regKey ).getFormattedText() ).getUnformattedText();
                break;
        }

        if( elementOne > 23 )
            offsetOne = 192;
        else if( elementOne > 15 )
            offsetOne = 128;
        else if( elementOne > 7 )
            offsetOne = 64;
        else
            offsetOne = 0;

        if( elementTwo > 23 )
            offsetTwo = 192;
        else if( elementTwo > 15 )
            offsetTwo = 128;
        else if( elementTwo > 7 )
            offsetTwo = 64;
        else
            offsetTwo = 0;
    }

    @Override
    public int getHeight()
    {
        int height = 11;

        for( String a : text )
        {
            height += 9;
        }

        if( height > 32 )
            return height;
        else
            return 32;
    }

    public void clickActionGlossary()
    {
//        LOGGER.info( "Clicked " + this.title + " Button" );
        GlossaryScreen.setButtonsToKey( regKey );
        Minecraft.getMinecraft().displayGuiScreen( new GlossaryScreen( Minecraft.getMinecraft().player.getUniqueID(), new TextComponentTranslation( "pmmo.glossary" ), false ) );
    }

    public void clickActionSkills()
    {
        if( !Skill.getSkill( regKey ).equals( Skill.INVALID_SKILL ) )
            Minecraft.getMinecraft().displayGuiScreen( new ListScreen( Minecraft.getMinecraft().player.getUniqueID(), new TextComponentTranslation( "" ), regKey, JType.HISCORE, Minecraft.getMinecraft().player ) );
    }

    @Override
    public void drawButton( Minecraft mc, int mouseX, int mouseY, float partialTicks )
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.hovered);
        GlStateManager.enableBlend();
        GlStateManager.defaultBlendFunc();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        minecraft.getTextureManager().bindTexture( buttons );
        this.drawTexturedModalRect(this.x, this.y, this.offsetOne + ( this.hovered ? 32 : 0 ), this.elementOne, this.width, this.height);
        minecraft.getTextureManager().bindTexture( items );
        this.drawTexturedModalRect(this.x, this.y, this.offsetTwo + ( this.hovered ? 32 : 0 ), this.elementTwo, this.width, this.height);
        if( !itemStack.getItem().equals( Items.AIR ) && entity == null )
            itemRenderer.renderItemIntoGUI( itemStack, this.x + 8, this.y + 8 );

        if( entity != null )
        {
            mobHeight = entity.height;
            mobWidth = entity.width;
            mobScale = 27;

            if( mobHeight > 0 )
                mobScale /= Math.max(mobHeight, mobWidth);

            GuiInventory.drawEntityOnScreen( this.x + this.width / 2, this.y + this.height - 2, (int) mobScale, mouseX, mouseY, entity );
        }

        this.renderBg(minecraft, x, y);
        int j = getFGColor();
        this.drawCenteredString(fontrenderer, this.buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}